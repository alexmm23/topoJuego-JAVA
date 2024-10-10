import javax.swing.*;

public class Welcome extends JFrame {
    private JLabel jlbTitle;
    private JButton jbnStart, jbnExit;
    private JComboBox<String> jcbDifficulty;
    private String selectedDifficulty;

    public Welcome() {
        setSize(400, 400);
        setTitle("TopoJuego");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        setLayout(null);
        jlbTitle = new JLabel("Bienvenido al TopoJuego");
        jlbTitle.setBounds(100, 20, 200, 30);
        add(jlbTitle);

        jbnStart = new JButton("Iniciar Juego");
        jbnStart.setBounds(100, 100, 200, 30);
        add(jbnStart);
        jbnStart.addActionListener(ActionListener -> {
            System.out.println("Iniciar juego");
            JFrame frame = new JFrame("Juego del Topo");
            TopoJuego topoJuego = new TopoJuego(selectedDifficulty);
            topoJuego.setVisible(true);
            frame.setSize(800, 600);
            frame.add(topoJuego);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            setVisible(false);
            dispose();
        });
        jbnExit = new JButton("Salir");
        jbnExit.setBounds(100, 150, 200, 30);
        add(jbnExit);
        jbnExit.addActionListener(e -> System.exit(0));

        initComboBox();
    }

    private void initComboBox() {
        jcbDifficulty = new JComboBox<>();
        jcbDifficulty.addItem("Fácil");
        jcbDifficulty.addItem("Medio");
        jcbDifficulty.addItem("Difícil");
        jcbDifficulty.setBounds(100, 200, 200, 30);
        jcbDifficulty.addActionListener(e -> selectedDifficulty = (String) jcbDifficulty.getSelectedItem());
        add(jcbDifficulty);
    }

}
