import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Random;

public class TopoJuego extends JPanel {

    private BufferedImage fondo;
    private Image mazo, topo;
    private int[][] hoyos = new int[6][2]; // Coordenadas de los hoyos
    private int posTopoX, posTopoY; // Coordenadas actuales del topo
    private int golpesAcertados = 0;
    private int tiempoRestante = 30, tiempoSelected; // Tiempo en segundos
    private Timer timer, topoMoverTimer;
    private Random random = new Random();
    private boolean juegoTerminado = false;
    private BufferedImage pasto;
    private long startTime;
    private String dificultad;
    private int dificultadSelected;
    private HashMap<String, Integer> dificultades = new HashMap<>();
    private HashMap<String, Integer> tiempos = new HashMap<>();

    public TopoJuego(String dificultad) {
        if (dificultad == null) {
            dificultad = "Fácil";
        }
        fillDificultades();
        fillTiempos();
        configurarHoyos();
        this.dificultad = dificultad;
        dificultadSelected = dificultades.get(this.dificultad);
        tiempoRestante = tiempos.get(this.dificultad);
        tiempoSelected = tiempos.get(this.dificultad);
        setPreferredSize(new Dimension(800, 600));
        try {
            pasto = ImageIO.read(new File("src/pasto.jpeg"));
        } catch (Exception e) {
            System.out.println("Error al cargar la imagen del pasto");
            e.printStackTrace();
            return;
        }
        mazo = new ImageIcon("src/mazo.png").getImage();
        topo = new ImageIcon("src/topo.png").getImage();
        fondo = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = fondo.createGraphics();
        g2.setPaint(new TexturePaint(pasto, new Rectangle(0, 0, 800, 600)));
        g2.fillRect(0, 0, fondo.getWidth(), fondo.getHeight());
        g2.dispose();

        iniciarTemporizador();
        iniciarMovimientoTopo();

        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(mazo, new Point(0, 0), "mazo"));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!juegoTerminado) {
                    int x = e.getX();
                    int y = e.getY();

                    if (x >= posTopoX && x <= posTopoX + topo.getWidth(null) &&
                            y >= posTopoY && y <= posTopoY + topo.getHeight(null)) {
                        golpesAcertados++;
                        hacerSonido("src/martillo.wav");
                        moverTopo();
                        repaint();
                        if (golpesAcertados == 5) {
                            juegoTerminado = true;
                            terminarJuego("¡Ganaste!");
                        }
                    }
                }
            }
        });
    }

    private void fillDificultades() {
        dificultades.put("Fácil", 2);
        dificultades.put("Medio", 10);
        dificultades.put("Difícil", 20);
    }

    private void fillTiempos() {
        tiempos.put("Fácil", 30);
        tiempos.put("Medio", 20);
        tiempos.put("Difícil", 10);
    }

    private void configurarHoyos() {
        hoyos[0] = new int[]{50, 100};
        hoyos[1] = new int[]{300, 150};
        hoyos[2] = new int[]{500, 190};
        hoyos[3] = new int[]{50, 400};
        hoyos[4] = new int[]{300, 400};
        hoyos[5] = new int[]{550, 400};
    }

    private void iniciarTemporizador() {
        timer = new Timer(1000 / 60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
                tiempoRestante = (int) (tiempoSelected - (System.currentTimeMillis() - startTime) / 1000);
                if (tiempoRestante <= 0 && !juegoTerminado) {
                    juegoTerminado = true;
                    terminarJuego("¡Perdiste! Se acabó el tiempo.");
                }
            }
        });
        startTime = System.currentTimeMillis();
        timer.start();
    }

    private void programarNuevoMovimiento() {

        int intervaloTiempo = random.nextInt(1500 / dificultadSelected) + 500;
        System.out.println("Intervalo de tiempo: " + intervaloTiempo);
        if (topoMoverTimer != null) {
            topoMoverTimer.stop();
        }
        topoMoverTimer = new Timer(intervaloTiempo, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!juegoTerminado) {
                    moverTopo();
                    programarNuevoMovimiento();
                }
            }
        });
        topoMoverTimer.setRepeats(false);
        topoMoverTimer.start();

    }

    private void iniciarMovimientoTopo() {
        moverTopo();
        programarNuevoMovimiento();
    }

    private void moverTopo() {
        int hoyoIndex = random.nextInt(hoyos.length);
        posTopoX = hoyos[hoyoIndex][0];
        posTopoY = hoyos[hoyoIndex][1];
        repaint();
    }

    private void hacerSonido(String ruta) {
        try {
            File archivo = new File(ruta);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(archivo);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            System.out.println("Error al reproducir el sonido");
            e.printStackTrace();
        }
    }

    private void terminarJuego(String mensaje) {
        timer.stop();
        topoMoverTimer.stop();
        JOptionPane.showMessageDialog(this, mensaje);
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Desea volver a jugar?");
        if (respuesta == JOptionPane.YES_OPTION) {
            Welcome w = new Welcome();
            w.setVisible(true);
            System.out.println("Volver a jugar");
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            System.out.println(frame != null);
            assert frame != null;
            frame.dispose();
        } else {
            System.exit(0);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g.drawImage(fondo, 0, 0, null);
        g.setColor(Color.BLACK);
        for (int[] hoyo : hoyos) {
            g2.setPaint(new GradientPaint(hoyo[0], hoyo[1], Color.BLACK, hoyo[0] + 120, hoyo[1] + 80, Color.GRAY));
            g.fillOval(hoyo[0], hoyo[1], 120, 80);
        }

        if (!juegoTerminado) {
            g.drawImage(topo, posTopoX, posTopoY, 80, 80, null);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("Golpes: " + golpesAcertados, 10, 20);
        g.drawString("Tiempo: " + tiempoRestante, 10, 50);
    }

    /*public static void main(String[] args) {
        JFrame frame = new JFrame("Juego del Topo");
        TopoJuego juego = new TopoJuego();
        frame.setSize(800, 600);
        frame.add(juego);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }*/
}
