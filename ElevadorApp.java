import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ElevadorApp extends JFrame {

    private static final int NUM_ANDARES = 10;
    private static final int NUM_ELEVADORES = 2;

    private JButton[] botoesAndar;
    private JButton[] botoesElevador;
    private JTextField andarDestinoTextField;
    private JPanel[] luzesElevador;
    private JPanel[] andarIndicators; // Barra vertical para indicar a posição do elevador em cada andar

    private int[] andarAtualElevador;
    private boolean[] emMovimento;

    public ElevadorApp() {
        // Configurações básicas da janela
        setTitle("Simulador de Elevadores");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        try {
            // Define o Look and Feel Nimbus para uma aparência mais moderna
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        luzesElevador = new JPanel[NUM_ELEVADORES];
        andarIndicators = new JPanel[NUM_ELEVADORES];
        for (int i = 0; i < NUM_ELEVADORES; i++) {
            botoesElevador[i] = new JButton("Elevador " + (i + 1));
            luzesElevador[i] = new JPanel();
            luzesElevador[i].setPreferredSize(new Dimension(20, 20));
            luzesElevador[i].setBackground(Color.RED);
            andarIndicators[i] = new JPanel();
            andarIndicators[i].setPreferredSize(new Dimension(20, 200)); // Tamanho da barra vertical
            andarIndicators[i].setBackground(Color.GRAY); // Cor da barra vertical (pode ser ajustada)
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
        JPanel painelLuzesElevador = new JPanel(new GridLayout(1, NUM_ELEVADORES));
        JPanel painelAndarIndicators = new JPanel(new GridLayout(1, NUM_ELEVADORES));
        for (int i = 0; i < NUM_ELEVADORES; i++) {
            painelBotoesElevador.add(botoesElevador[i]);
            painelLuzesElevador.add(luzesElevador[i]);
            painelAndarIndicators.add(andarIndicators[i]);
        }

        JPanel painelControles = new JPanel(new BorderLayout());
        painelControles.add(painelAndares, BorderLayout.WEST);
        painelControles.add(andarDestinoTextField, BorderLayout.CENTER);
        painelControles.add(painelBotoesElevador, BorderLayout.EAST);
        painelControles.add(painelLuzesElevador, BorderLayout.SOUTH);

        add(painelControles, BorderLayout.NORTH); // Movido para o norte para deixar espaço para as barras verticais
        add(painelAndarIndicators, BorderLayout.CENTER); // Adiciona as barras verticais ao centro

        // Ajusta o tamanho da janela automaticamente
        pack();

        // Centraliza a janela na tela
        setLocationRelativeTo(null);

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
            System.out.println("Elevador " + (elevadorMaisProximo + 1) + " a caminho do Andar " + andar);
            emMovimento[elevadorMaisProximo] = true;

            // Atualiza a luz indicativa para verde (indicando movimento)
            luzesElevador[elevadorMaisProximo].setBackground(Color.GREEN);

            // Atualiza a posição da barra vertical para indicar a posição do elevador
            andarIndicators[elevadorMaisProximo].setPreferredSize(new Dimension(20, (andar + 1) * 20));

            // Simula o movimento do elevador (atualize conforme necessário)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int andarAtual = andarAtualElevador[elevadorMaisProximo];
                        int destino = andar;

                        System.out.println("Elevador " + (elevadorMaisProximo + 1) + " em movimento do Andar " +
                                andarAtual + " para o Andar " + destino);

                        // Simula o tempo que leva para o elevador se mover
                        Thread.sleep(Math.abs(destino - andarAtual) * 1000);

                        andarAtualElevador[elevadorMaisProximo] = destino; // Atualiza o andar atual após o movimento
                        emMovimento[elevadorMaisProximo] = false;

                        // Atualiza a luz indicativa para vermelho (indicando que o elevador está parado)
                        luzesElevador[elevadorMaisProximo].setBackground(Color.RED);

                        // Reseta a posição da barra vertical para indicar que o elevador parou
                        andarIndicators[elevadorMaisProximo].setPreferredSize(new Dimension(20, (destino + 1) * 20));

                        System.out.println("Elevador " + (elevadorMaisProximo + 1) + " chegou ao Andar " + destino);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
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

                // Atualiza a luz indicativa para verde (indicando movimento)
                luzesElevador[elevador].setBackground(Color.GREEN);

                // Atualiza a posição da barra vertical para indicar a posição do elevador
                andarIndicators[elevador].setPreferredSize(new Dimension(20, (andarDestino + 1) * 20));

                // Simula o movimento do elevador (atualize conforme necessário)
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000); // Simula o tempo que leva para o elevador se mover
                            andarAtualElevador[elevador] = andarDestino;
                            emMovimento[elevador] = false;

                            // Atualiza a luz indicativa para vermelho (indicando que o elevador está parado)
                            luzesElevador[elevador].setBackground(Color.RED);

                            // Reseta a posição da barra vertical para indicar que o elevador parou
                            andarIndicators[elevador].setPreferredSize(new Dimension(20, (andarDestino + 1) * 20));

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
