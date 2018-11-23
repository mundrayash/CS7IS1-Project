import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Window extends JFrame{
    private String[] questions = {"Nothing for now", "Still nothing"};

    public Window()
    {
        prepareGUI();
    }

    public void launchWindow() {
        this.setVisible(true);
    }

    private void prepareGUI()
    {
        int windowWidth = 800;
        int windowHeight = 600;
        int margin = 20;

        final JList questionList = new JList(questions);
        questionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        questionList.setVisibleRowCount(0);
        questionList.setSelectedIndex(0);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(questionList);
        scrollPane.setSize(windowWidth - margin * 4 - 100, 200);

        JButton button = new JButton("Execute");
        button.setSize(100, 50);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        final JTextArea resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        JScrollPane resultPane = new JScrollPane();
        resultPane.setViewportView(resultTextArea);
        resultPane.setSize(windowWidth - margin * 2, windowHeight - 200 - margin * 4);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int idx = questionList.getSelectedIndex();
                resultTextArea.setText("Work in progress!!");
            }
        });

        panel.add(scrollPane);
        panel.add(button);
        panel.add(resultPane);

        this.setTitle("Testing");
        this.setBounds(100, 100, windowWidth, windowHeight);
        this.setResizable(false);
        this.getContentPane().add(panel, "Center");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(false);
    }
}
