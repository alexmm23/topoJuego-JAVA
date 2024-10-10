
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
import java.util.Random;
import java.util.TimerTask;

public class TopoJuego extends JPanel {

    private BufferedImage fondo;
    private Image mazo, topo;
    private int[][] hoyos = new int[6][2]; // Coordenadas de los hoyos
    private int posTopoX, posTopoY; // Coordenadas actuales del topo
    private int golpesAcertados = 0;
    private int tiempoRestante = 30; // Tiempo en segundos
    private Timer timer;
    private Random random = new Random();
    private boolean juegoTerminado = false;
    private BufferedImage pasto;
    private long startTime;

    public TopoJuego() {
        try {
            pasto = ImageIO.read(new File("src/pasto.jpeg"));
        } catch (Exception e) {
            System.out.println("Error al cargar la imagen del pasto");
            e.printStackTrace();
            return;
        }

        // Cargar imágenes de mazo y topo
        mazo = new ImageIcon("src/mazo.png").getImage();
        topo = new ImageIcon("src/topo.png").getImage();
        // Crear fondo de pasto (textura)
        fondo = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = fondo.createGraphics();
        g2.setPaint(new TexturePaint(pasto, new Rectangle(0, 0, 800, 600)));
        g2.fillRect(0, 0, fondo.getWidth(), fondo.getHeight());
        g2.dispose();

        // Configurar hoyos
        configurarHoyos();

        // Iniciar temporizador
        iniciarTemporizador();
        moverTopo();

        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(mazo, new Point(0, 0), "mazo"));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Mouse clickeado en: " + e.getX() + ", " + e.getY());
                if (!juegoTerminado) {
                    int x = e.getX();
                    int y = e.getY();

                    if (x >= posTopoX && x <= posTopoX + topo.getWidth(null) &&
                            y >= posTopoY && y <= posTopoY + topo.getHeight(null)) {
                        golpesAcertados++;
                        hacerSonido("src/golpe.wav");
                        moverTopo();
                        repaint();
                        if (golpesAcertados == 3) {
                            juegoTerminado = true;
                            terminarJuego("¡Ganaste!");
                        }
                    }
                }
            }
        });

    }

    private void configurarHoyos() {
        hoyos[0] = new int[]{100, 150};
        hoyos[1] = new int[]{300, 150};
        hoyos[2] = new int[]{500, 150};
        hoyos[3] = new int[]{100, 300};
        hoyos[4] = new int[]{300, 300};
        hoyos[5] = new int[]{500, 300};
    }

    private void iniciarTemporizador() {
        // Temporizador del juego (30 segundos)
        timer = new Timer(1000 / 60, null); // Actualización a 60 FPS
        startTime = System.currentTimeMillis();
        timer.start();

    }

    private void moverTopo() {
        // Traslación aleatoria del topo a un hoyo
        int hoyoIndex = random.nextInt(hoyos.length);
        posTopoX = hoyos[hoyoIndex][0];
        posTopoY = hoyos[hoyoIndex][1];
        System.out.println("Topo movido a: " + posTopoX + ", " + posTopoY);
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
        JOptionPane.showMessageDialog(this, mensaje);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(fondo, 0, 0, null);
        g.setColor(Color.BLACK);
        for (int[] hoyo : hoyos) {
            g.fillOval(hoyo[0], hoyo[1], 80, 80);
        }

        if (!juegoTerminado) {
            g.drawImage(topo, posTopoX, posTopoY, 80, 80, null);
        }

        tiempoRestante = (int) (30 - (System.currentTimeMillis() - startTime) / 1000);
        // Mostrar golpes acertados y tiempo restante
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Golpes: " + golpesAcertados, 10, 20);
        g.drawString("Tiempo: " + tiempoRestante, 10, 40);
        // Verificar si el juego terminó
        if (System.currentTimeMillis() - startTime >= 30000 || golpesAcertados >= 3) {
            timer.stop();
            juegoTerminado = true;
        }
        if (System.currentTimeMillis() - startTime >= 3000 && !juegoTerminado) {
            try {
                Thread.sleep(1000);
                moverTopo();
                repaint();
                Thread.yield();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        revalidate();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Juego del Topo");
        TopoJuego juego = new TopoJuego();
        frame.setSize(800, 600);
        frame.add(juego);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
