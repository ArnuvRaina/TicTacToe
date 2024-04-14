import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class TicTacToeGUI extends JFrame implements ActionListener {
    private JButton[][] buttons;
    private JLabel statusLabel;
    private int[][] board;
    private int currentPlayer;
    private int player1Wins, player2Wins;
    private JButton replayButton;
    private JRadioButton humanVsHumanRadioButton;
    private JRadioButton humanVsComputerRadioButton;

    public TicTacToeGUI() {
        setTitle("Tic Tac Toe");
        setSize(440, 580);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        boardPanel.setBounds(70, 100, 300, 300);
        buttons = new JButton[3][3];
        board = new int[3][3];

        currentPlayer = 1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(new Font("Comic Sans MS", Font.BOLD, 40));
                buttons[i][j].addActionListener(this);
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setBackground(new Color(255, 222, 173));
                buttons[i][j].setForeground(Color.BLACK);
                buttons[i][j].setBorder(new CompoundBorder(
                        BorderFactory.createRaisedBevelBorder(),
                        BorderFactory.createLoweredBevelBorder())
                );
                boardPanel.add(buttons[i][j]);
            }
        }
        add(boardPanel);

        statusLabel = new JLabel("Player 1's turn");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        statusLabel.setBounds(70, 30, 300, 30);
        statusLabel.setForeground(Color.WHITE);
        add(statusLabel);

        JPanel controlPanel = new JPanel();
        controlPanel.setBounds(70, 420, 300, 80);
        controlPanel.setBackground(new Color(70, 130, 180));
        replayButton = new JButton("Replay");
        replayButton.addActionListener(this);
        replayButton.setFocusPainted(false);
        replayButton.setBackground(new Color(255, 69, 0));
        replayButton.setForeground(Color.WHITE);
        replayButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        replayButton.setPreferredSize(new Dimension(100, 40));
        controlPanel.add(replayButton);
        add(controlPanel);

        JPanel modePanel = new JPanel();
        modePanel.setBounds(70, 500, 300, 30);
        modePanel.setBackground(new Color(30, 144, 255));
        humanVsHumanRadioButton = new JRadioButton("Human vs Human");
        humanVsHumanRadioButton.setSelected(true);
        humanVsHumanRadioButton.setBackground(new Color(30, 144, 255));
        humanVsHumanRadioButton.setForeground(Color.WHITE);
        humanVsHumanRadioButton.addActionListener(this);
        modePanel.add(humanVsHumanRadioButton);

        humanVsComputerRadioButton = new JRadioButton("Human vs Computer");
        humanVsComputerRadioButton.setBackground(new Color(30, 144, 255));
        humanVsComputerRadioButton.setForeground(Color.WHITE);
        humanVsComputerRadioButton.addActionListener(this);
        modePanel.add(humanVsComputerRadioButton);

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(humanVsHumanRadioButton);
        modeGroup.add(humanVsComputerRadioButton);

        add(modePanel);

        getContentPane().setBackground(new Color(30, 144, 255));

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == replayButton) {
            resetGame();
        } else if (source == humanVsHumanRadioButton || source == humanVsComputerRadioButton) {
            resetGame();
            if (humanVsComputerRadioButton.isSelected() && currentPlayer == 2) {
                makeComputerMove();
            }
        } else {
            JButton button = (JButton) source;
            int row = -1, col = -1;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (buttons[i][j] == button) {
                        row = i;
                        col = j;
                        break;
                    }
                }
            }

            if (row != -1 && col != -1 && board[row][col] == 0) {
                button.setText(currentPlayer == 1 ? "X" : "O");
                board[row][col] = currentPlayer;
                if (checkWin(currentPlayer)) {
                    if (currentPlayer == 1) {
                        player1Wins++;
                        JOptionPane.showMessageDialog(this, "Player 1 wins!", "Winner", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        player2Wins++;
                        JOptionPane.showMessageDialog(this, "Player 2 wins!", "Winner", JOptionPane.INFORMATION_MESSAGE);
                    }
                    disableAllButtons();
                } else if (checkDraw()) {
                    JOptionPane.showMessageDialog(this, "It's a draw!", "Draw", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    currentPlayer = currentPlayer == 1 ? 2 : 1;
                    statusLabel.setText(currentPlayer == 1 ? "Player 1's turn" : "Player 2's turn");
                    if (humanVsComputerRadioButton.isSelected() && currentPlayer == 2) {
                        makeComputerMove();
                    }
                }
            }
        }
    }

    private void makeComputerMove() {
        final int[] row = new int[1];
        final int[] col = new int[1];

        new Thread(() -> {
            try {
                Thread.sleep(2000); // Delay for 2 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Random rand = new Random();
            do {
                row[0] = rand.nextInt(3);
                col[0] = rand.nextInt(3);
            } while (board[row[0]][col[0]] != 0);

            SwingUtilities.invokeLater(() -> {
                buttons[row[0]][col[0]].setText("O");
                board[row[0]][col[0]] = 2;

                if (checkWin(2)) {
                    player2Wins++;
                    JOptionPane.showMessageDialog(this, "Computer wins!", "Winner", JOptionPane.INFORMATION_MESSAGE);
                    disableAllButtons();
                } else if (checkDraw()) {
                    JOptionPane.showMessageDialog(this, "It's a draw!", "Draw", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    currentPlayer = 1;
                    statusLabel.setText("Player 1's turn");
                }
            });
        }).start();
    }

    private boolean checkWin(int player) {
        for (int i = 0; i < 3; i++) {
            if ((board[i][0] == player && board[i][1] == player && board[i][2] == player) ||
                    (board[0][i] == player && board[1][i] == player && board[2][i] == player))
                return true;
        }
        if ((board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
                (board[0][2] == player && board[1][1] == player && board[2][0] == player))
            return true;

        return false;
    }

    private boolean checkDraw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0)
                    return false;
            }
        }
        return true;
    }

    private void disableAllButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    private void resetGame() {
        currentPlayer = 1;
        player1Wins = 0;
        player2Wins = 0;
        statusLabel.setText("Player 1's turn");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
                board[i][j] = 0;
            }
        }
    }

    public static void main(String[] args) {
        new TicTacToeGUI();
    }
}
