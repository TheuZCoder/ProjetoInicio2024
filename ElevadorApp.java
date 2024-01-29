import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ElevadorApp extends JFrame {

    private static final int NUM_ANDARES = 8;
    private static final int NUM_ELEVADORES = 2;

    private JButton[] botoesAndar;
    private JButton[] botoesElevador;
    private JTextField andarDestinoTextField;

    private int[] andarAtualElevador;
    private boolean[] emMovimento;

    public ElevadorApp() {
        // Configurações básicas da janela
        setTitle("Simulador de Elevadores");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        andarAtualElevador = new int[NUM_ELEVADORES];
        emMovimento = new boolean[NUM_ELEVADORES];

        // Inicializa os botões dos andares
        botoesAndar = new JButton[NUM_ANDARES];
        for (int i = 0; i < NUM_ANDARES; i++) {
            botoesAndar[i] = new JButton("Andar " + i);
            final int andar = i;
            botoesAndar[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chamarElevador(andar);
                }
            });
        }

        // Inicializa os botões dos elevadores
        botoesElevador = new JButton[NUM_ELEVADORES];
        for (int i = 0; i < NUM_ELEVADORES; i++) {
            botoesElevador[i] = new JButton("Elevador " + (i + 1));
            final int elevador = i;
            botoesElevador[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    definirAndarDestino(elevador);
                }
            });
        }
        

        // Inicializa o campo de texto para o andar de destino
        andarDestinoTextField = new JTextField();

        // Adiciona os componentes à janela
        JPanel painelAndares = new JPanel(new GridLayout(NUM_ANDARES, 1));
        for (int i = NUM_ANDARES - 1; i >= 0; i--) {
            painelAndares.add(botoesAndar[i]);
        }

        JPanel painelBotoesElevador = new JPanel(new GridLayout(1, NUM_ELEVADORES));
        for (int i = 0; i < NUM_ELEVADORES; i++) {
            painelBotoesElevador.add(botoesElevador[i]);
        }

        JPanel painelControles = new JPanel(new BorderLayout());
        painelControles.add(painelAndares, BorderLayout.WEST);
        painelControles.add(andarDestinoTextField, BorderLayout.CENTER);
        painelControles.add(painelBotoesElevador, BorderLayout.EAST);

        add(painelControles);

        // Exibe a janela
        setVisible(true);
    }

    private void chamarElevador(int andar) {
        int elevadorMaisProximo = encontrarElevadorMaisProximo(andar);
        System.out.println("Chamando elevador para o Andar " + andar);
        
        if (emMovimento[elevadorMaisProximo]) {
            // Se o elevador estiver em movimento, ignore o chamado
            System.out.println("Elevador " + (elevadorMaisProximo + 1) + " está em movimento.");
        } else {
            // Atualiza o andar atual do elevador e exibe mensagem
            andarAtualElevador[elevadorMaisProximo] = andar;
            System.out.println("Elevador " + (elevadorMaisProximo + 1) + " a caminho do Andar " + andar);
        }
    }

    private int encontrarElevadorMaisProximo(int andar) {
        // Lógica para encontrar o elevador mais próximo
        int elevadorMaisProximo = 0;
        int distanciaMinima = Math.abs(andar - andarAtualElevador[0]);

        for (int i = 1; i < NUM_ELEVADORES; i++) {
            int distancia = Math.abs(andar - andarAtualElevador[i]);
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                elevadorMaisProximo = i;
            }
        }

        return elevadorMaisProximo;
    }

    private void definirAndarDestino(int elevador) {
        if (emMovimento[elevador]) {
            // Se o elevador estiver em movimento, ignore a solicitação
            System.out.println("Elevador " + (elevador + 1) + " está em movimento.");
        } else {
            String andarDestinoStr = andarDestinoTextField.getText();

            try {
                int andarDestino = Integer.parseInt(andarDestinoStr);
                // Atualiza o andar de destino do elevador e exibe mensagem
                System.out.println("Elevador " + (elevador + 1) + " indo para o Andar " + andarDestino);
                emMovimento[elevador] = true;

                // Simula o movimento do elevador (atualize conforme necessário)
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000); // Simula o tempo que leva para o elevador se mover
                            andarAtualElevador[elevador] = andarDestino;
                            emMovimento[elevador] = false;
                            System.out.println("Elevador " + (elevador + 1) + " chegou ao Andar " + andarDestino);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (NumberFormatException e) {
                System.out.println("Informe um número válido para o andar de destino.");
            }
        }
        String andarDestinoStr = andarDestinoTextField.getText();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ElevadorApp();
            }
        });
    }
}
