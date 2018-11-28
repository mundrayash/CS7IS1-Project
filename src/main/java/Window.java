import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;


public class Window extends JFrame{
    private String[] questions;
    private QueryHandler handler;

    public Window() throws IOException {
        prepareGUI();
        handler = new QueryHandler();
    }

    public void launchWindow() {
        this.setVisible(true);
    }


    private void prepareGUI() throws IOException {
        int windowWidth = 800;
        int windowHeight = 600;
        int margin = 20;

        final JList questionList = new JList(getQuestions());
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
                int index = questionList.getSelectedIndex();
                String result = handler.executeQuery(index);
                resultTextArea.setText(result);
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

    private String[] getQuestions() throws IOException
    {
        File directory = new File(getClass().getResource("questions").getFile());
        File[] files = directory.listFiles();

        Arrays.sort(files);
        questions = new String[files.length];

        for (int i = 0; i < files.length; i++) {
            questions[i] = new String(Files.readAllBytes(Paths.get(files[i].getPath())), StandardCharsets.UTF_8);
        }

        return  questions;
    }
}
