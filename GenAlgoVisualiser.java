import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GenAlgoVisualiser extends JFrame implements ActionListener{
    
    private int generationAmount = 10,
                generationSize = 10,
                parentAmount = 2,
                childSize;
    private Supplier<Boolean> geneSupplier = new Random()::nextBoolean;
    private Function<List<Boolean>, Integer> childScorer = Backpack::getPriceIfAllowed;

    private List<List<Boolean>> currentGen;
    private List<Integer> scores = new ArrayList<Integer>();

    private String input = generationAmount + "";

    static private enum State {GEN_AMOUNT, GEN_SIZE, PAR_AMOUNT, RUNNING, ITEM_NAME, ITEM_WEIGTH, ITEM_PRICE}
    private State state = State.GEN_AMOUNT;

    private KeyAdapter keyAdapter = new KeyAdapter(){
                                            public void keyPressed(KeyEvent k) {
                                                keyPress(k);
                                            }
                                    };

    private JPanel panel = new JPanel();
    private JLabel inputDesc = new JLabel(),
                   genAmntDesc = new JLabel("Antal af generationer:"),
                   genSizeDesc = new JLabel("Størrelse af generationerne:"),
                   parAmntDesc = new JLabel("Antal af forældre:"),
                   newScoreDesc = new JLabel(),
                   crntItemsDesc = new JLabel(),
                   scoresDesc = new JLabel(),
                   newItemDesc = new JLabel(),
                   itemListDesc = new JLabel("De nuværende genstande på huskelisten er: (Tryk for at fjerne dem)");
    
    private JPanel itemButtons = new JPanel(); 

    private Backpack.Item newItem;

    public GenAlgoVisualiser() {
        initializeWindow();
        addKeyListener(keyAdapter);
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);
        FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER);
        itemButtons.setLayout(flowLayout);
        initializeAlgorithmLabels();
    }

    private void initializeWindow() {
        setSize(1000, 500);
        setLocation(500, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setTitle("Genetic Algorithm Visualisation");
    }

    private void initializeAlgorithmLabels() {
        add(panel);
        panel.removeAll();
        panel.add(inputDesc);
        panel.add(new JLabel(" "));
        panel.add(genAmntDesc);
        panel.add(genSizeDesc);
        panel.add(parAmntDesc);
        panel.add(new JLabel(" "));
        panel.add(newScoreDesc);
        panel.add(crntItemsDesc);
        panel.add(scoresDesc);
    }

    private void initializeBackpackLabels() {
        add(panel);
        panel.removeAll();
        panel.add(inputDesc);
        panel.add(newItemDesc);
        panel.add(new JLabel(" "));
        panel.add(itemListDesc);
        itemButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(itemButtons);
    }

    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLACK);
        switch(state) {
            case GEN_AMOUNT:
                paintGenAmount();
                break;
            case GEN_SIZE:
                paintGenSize();
                break;
            case PAR_AMOUNT:
                paintParAmount();
                break;
            case RUNNING:
                paintRunning();
                break;
            case ITEM_NAME:
                paintItemName();
                break;
            case ITEM_WEIGTH:
                paintItemWeight();
                break;
            case ITEM_PRICE:
                paintItemPrice();
                break;
            default:
                break;
        }
        paintGraph(g);
    }

    private void paintGenAmount() {
        inputDesc.setText("Skriv antallet af generationer, der skal udføres, og tryk enter. " + "Nuværende input: " + input);
    }

    private void paintGenSize() {
        inputDesc.setText("Skriv størrelsen på hver generation og tryk enter. " + "Nuværende input: " + input);
        genAmntDesc.setText("Antal af generationer: " + generationAmount);
    }

    private void paintParAmount() {
        inputDesc.setText("Skriv antallet af forældre til hver generation og tryk enter. " + "Nuværende input: " + input);
        genSizeDesc.setText("Størrelse af generationerne: " + generationSize);
    }

    private void paintRunning() {
        inputDesc.setText("Mellemrum: Kør algoritmen med samme indstillinger. " +
                          "Enter: Kør algoritmen med nye indstillinger. " +
                          "Delete: Start algoritmen forfra. " +
                          "Control: Redigér listen af genstande.");
        newScoreDesc.setText("Værdien på den bedste taske-kombinationer var " + scores.get(scores.size() - 1) + ".");
        parAmntDesc.setText("Antal af forældre: " + parentAmount);
    }

    private void paintGraph(Graphics g) {
        if(scores.size() <= 1) return;

        float max = scores.stream().max(Integer::compare).get();
        float min = scores.stream().min(Integer::compare).get();

        if(min == max) return;

        float spread = (float) getWidth() / (scores.size() - 1);

        int[] xPoints = new int[scores.size() + 2];
        int[] yPoints = new int[scores.size() + 2];

        yPoints[0] = getHeight();

        xPoints[xPoints.length - 1] = getWidth();
        yPoints[yPoints.length - 1] = getHeight();

        for(int i = 0; i < scores.size(); i++) {
            xPoints[i + 1] = (int) (i * spread);
            yPoints[i + 1] = (int) (getHeight() - (
                                        (scores.get(i) - min) /
                                        (max           - min) *
                                        getHeight() * 3/5));
        }

        g.setColor(Color.RED);
        g.fillPolygon(xPoints, yPoints, scores.size() + 2);
    }

    private void paintItemName() {
        inputDesc.setText("For at tilføje en ny genstand til listen, så skriv navnet og tryk enter. " + "Nuværende input: " + input);
        newItemDesc.setText("Ny genstand: " + newItem.toString());
        revalidate();
    }

    private void updateItemButtons() {
        itemButtons.removeAll();
        for(int i = 0; i < Backpack.getItemSizeFull(); i++) {
            JButton button = new JButton(Backpack.items.get(i).toString());
            button.setActionCommand(i + "");
            button.addActionListener(this);
            itemButtons.add(button);
        }
    }

    private void paintItemWeight() {
        inputDesc.setText("Skriv genstandens vægt og tryk enter. " + "Nuværende input: " + input);
        newItemDesc.setText("Ny genstand: " + newItem.toString());
    }

    private void paintItemPrice() {
        inputDesc.setText("Skriv genstandens pris og tryk enter. " + "Nuværende input: " + input);
        newItemDesc.setText("Ny genstand: " + newItem.toString());
    }

    private void resetAlgorithm() {
        currentGen = null;
        scores.clear();
        revalidate();
        repaint();
    }

    private void restartAlgorithm() {
        scores.clear();
        inputDesc.setText(" ");
        newScoreDesc.setText(" ");
        scoresDesc.setText(" ");
        currentGen = null;
    }

    private void runAlgorithm() {
        childSize = Backpack.getItemSizeFull();
        if(currentGen == null)
            currentGen = GeneticAlgorithm.makeGenerations(generationAmount, generationSize, parentAmount, childSize, geneSupplier, childScorer);
        else
            currentGen = GeneticAlgorithm.makeGenerationsFromGeneration(currentGen, generationAmount, generationSize, parentAmount, geneSupplier, childScorer);
        List<Boolean> bestChild = GeneticAlgorithm.getBestChild(currentGen, childScorer);
        scores.add(Backpack.getPriceIfAllowed(bestChild));
        crntItemsDesc.setText(Backpack.getNames(bestChild));
        String text = "<html>De bedste taske-værdier har været: ";
        for(int i : scores)
            text += i + " ";
        text += "</html>";
        scoresDesc.setText(text);
    }

    private void keyPress(KeyEvent k) {
        switch(k.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                dispose();
                return;
            case KeyEvent.VK_DELETE:
                restartAlgorithm();
                state = State.GEN_AMOUNT;
                repaint();
                return;
            case KeyEvent.VK_SPACE:
                runAlgorithm();
                repaint();
                return;
            case KeyEvent.VK_CONTROL:
                swapAlgorithmAndBackpackTab();
                repaint();
                return;
            default:
                break;
        }
        if(isEnter(k)) {
            updateState(k);
            repaint();
            return;
        }
        if(state != State.RUNNING) {
            if(updateInput(k)) {
                repaint();
                return;
            }
        }
    }

    private void swapAlgorithmAndBackpackTab() {
        if(state == State.ITEM_NAME || state == State.ITEM_WEIGTH || state == State.ITEM_PRICE) {
            initializeAlgorithmLabels();
            input = generationAmount + "";
            state = State.GEN_AMOUNT;
        }
        else {
            initializeBackpackLabels();
            updateItemButtons();
            newItem = new Backpack.Item("", 0, 0);
            input = "";
            state = State.ITEM_NAME;
        }
    }

    private void updateState(KeyEvent k) {
        switch(state) {
            case GEN_AMOUNT:
                try{ generationAmount = Integer.parseInt(input); } catch(NumberFormatException e) {}
                input = generationSize + "";
                state = State.GEN_SIZE;
                break;
            case GEN_SIZE:
                try{ generationSize = Integer.parseInt(input); } catch(NumberFormatException e) {}
                input = parentAmount + "";
                state = State.PAR_AMOUNT;
                break;
            case PAR_AMOUNT:
                try{ parentAmount = Integer.parseInt(input); } catch(NumberFormatException e) {}
                input = generationAmount + "";
                state = State.RUNNING;
                runAlgorithm();
                break;
            case RUNNING:
                state = State.GEN_AMOUNT;
                break;
            case ITEM_NAME:
                newItem.setName(input);
                input = "";
                state = State.ITEM_WEIGTH;
                break;
            case ITEM_WEIGTH:
                newItem.setWeight(Integer.parseInt(input));
                input = "";
                state = State.ITEM_PRICE;
                break;
            case ITEM_PRICE:
                newItem.setPrice(Integer.parseInt(input));
                Backpack.addItem(newItem);
                input = generationSize + "";
                state = State.GEN_AMOUNT;
                resetAlgorithm();
                initializeAlgorithmLabels();
                break;
            default:
                break;
        }
    }

    private boolean updateInput(KeyEvent k) {
        if(k.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            input = input.length() != 0 ? input.substring(0, input.length() - 1) : input;
            return true;
        }
        if((state == State.ITEM_NAME) ? isNumber(k) || isLetter(k) : isNumber(k)) {
            input += k.getKeyChar();
            return true;
        }
        return false;
    }

    private boolean isNumber(KeyEvent k) {
        return k.getKeyChar() >= '0' && k.getKeyChar() <= '9';
    }

    private boolean isLetter(KeyEvent k) {
        return (k.getKeyChar() >= 'a' && k.getKeyChar() <= 'z') || (k.getKeyChar() >= 'A' && k.getKeyChar() <= 'Z');
    }
    
    private boolean isEnter(KeyEvent k) {
        return k.getKeyCode() == KeyEvent.VK_ENTER;
    }

    public void actionPerformed(ActionEvent e) {
        if(itemButtons.getComponentCount() <= 1) return;
        int index = Integer.parseInt(e.getActionCommand());
        Backpack.removeItem(index);
        itemButtons.remove(index);
        this.requestFocusInWindow();
        updateItemButtons();
        resetAlgorithm();
    }
}
