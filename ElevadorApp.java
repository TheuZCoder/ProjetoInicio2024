import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.io.PrintStream;

public class ElevadorApp extends JFrame {

    private static final int NUM_ANDARES = 10;
    private static final int NUM_ELEVADORES = 2;

    private JButton[] botoesAndar;
    private JButton[] botoesElevador;
    private JTextField andarDestinoTextField;
    private JPanel[] luzesElevador;
    private JPanel[] andarIndicators;
    private JTextArea consoleTextArea; // Componente de texto para exibir a saída do console
    private PrintStream printStream; // Stream para redirecionar a saída do console

    private int[] andarAtualElevador;
    private boolean[] emMovimento;

    public ElevadorApp() {
        // Configurações básicas da janela
        setTitle("Simulador de Elevadores");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        try {
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
            andarIndicators[i].setPreferredSize(new Dimension(20, 200));
            andarIndicators[i].setBackground(Color.GRAY);
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

        // Inicializa o componente de texto para a saída do console
        consoleTextArea = new JTextArea();
        consoleTextArea.setEditable(false); // Impede que o usuário edite o texto
        JScrollPane scrollPane = new JScrollPane(consoleTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Redireciona a saída do console para o componente de texto
        printStream = new PrintStream(new CustomOutputStream(consoleTextArea));
        System.setOut(printStream);

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

        add(painelControles, BorderLayout.NORTH);
        add(painelAndarIndicators, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH); // Adiciona a caixa de texto para a saída do console

        // Ajusta o tamanho da janela automaticamente
        pack();

        // Centraliza a janela na tela
        setLocationRelativeTo(null);

        // Exibe a janela
        setVisible(true);
    }

    private void chamarElevador(int andar) {
        int elevadorMaisProximo = encontrarElevadorMaisProximo(andar);
        consoleTextArea.append("Chamando elevador para o Andar " + andar + "\n");

        if (emMovimento[elevadorMaisProximo]) {
            consoleTextArea.append("Elevador " + (elevadorMaisProximo + 1) + " está em movimento.\n");
        } else {
            consoleTextArea.append("Elevador " + (elevadorMaisProximo + 1) + " a caminho do Andar " + andar + "\n");
            emMovimento[elevadorMaisProximo] = true;
            luzesElevador[elevadorMaisProximo].setBackground(Color.GREEN);
            andarIndicators[elevadorMaisProximo].setPreferredSize(new Dimension(20, (andar + 1) * 20));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int andarAtual = andarAtualElevador[elevadorMaisProximo];
                        int destino = andar;

                        consoleTextArea.append("Elevador " + (elevadorMaisProximo + 1) +
                                " em movimento do Andar " + andarAtual + " para o Andar " + destino + "\n");

                        Thread.sleep(Math.abs(destino - andarAtual) * 1000);

                        andarAtualElevador[elevadorMaisProximo] = destino;
                        emMovimento[elevadorMaisProximo] = false;
                        luzesElevador[elevadorMaisProximo].setBackground(Color.RED);
                        andarIndicators[elevadorMaisProximo].setPreferredSize(new Dimension(20, (destino + 1) * 20));

                        consoleTextArea.append("Elevador " + (elevadorMaisProximo + 1) +
                                " chegou ao Andar " + destino + "\n");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private int encontrarElevadorMaisProximo(int andar) {
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
            consoleTextArea.append("Elevador " + (elevador + 1) + " está em movimento.\n");
        } else {
            String andarDestinoStr = andarDestinoTextField.getText();

            try {
                int andarDestino = Integer.parseInt(andarDestinoStr);
                consoleTextArea.append("Elevador " + (elevador + 1) + " indo para o Andar " + andarDestino + "\n");
                emMovimento[elevador] = true;
                luzesElevador[elevador].setBackground(Color.GREEN);
                andarIndicators[elevador].setPreferredSize(new Dimension(20, (andarDestino + 1) * 20));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            andarAtualElevador[elevador] = andarDestino;
                            emMovimento[elevador] = false;
                            luzesElevador[elevador].setBackground(Color.RED);
                            andarIndicators[elevador].setPreferredSize(new Dimension(20, (andarDestino + 1) * 20));

                            consoleTextArea.append("Elevador " + (elevador + 1) +
                                    " chegou ao Andar " + andarDestino + "\n");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (NumberFormatException e) {
                consoleTextArea.append("Informe um número válido para o andar de destino.\n");
            }
        }
    }

    // Classe para redirecionar a saída do console para o JTextArea
    private class CustomOutputStream extends OutputStream {
        private JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) {
            textArea.append(String.valueOf((char) b));
            textArea.setCaretPosition(textArea.getDocument().getLength());
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
