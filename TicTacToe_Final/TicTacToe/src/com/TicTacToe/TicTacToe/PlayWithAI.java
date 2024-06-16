package com.TicTacToe.TicTacToe;

import com.TicTacToe.EasyBot;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

import static com.TicTacToe.JFrameMain.jFrame;

/**
 * Tic-Tac-Toe: Two-player Graphics version with Simple-OO
 */
@SuppressWarnings("serial")
public class PlayWithAI extends Play2Players{
   public enum Bot{
      EASY_BOT, HEURISTIC_BOT
   }
   public static int rowBotPreSelected = -1;
   public static int colBotPreSelected = -1;
   public static int rowBotPreDiLai;
   public static int colBotPreDiLai;

   public static Bot GameBot;

   public static String PlayerName;

   // Named-constants of the various dimensions used for graphics drawing

   /** Constructor to setup the game and the GUI components */
   public PlayWithAI(String name){
      super(name,"");
   }

   @Override
   protected void PlayGame(String name1, String name2) {
      SetUpBoard(newRow);
      Player1Name = name1;
      Player2Name ="Máy";
      canvas = new DrawCanvas();  // Construct a drawing canvas (a JPanel)
      canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

      // The canvas (JPanel) fires a MouseEvent upon mouse-click
      canvas.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {  // mouse-clicked handler
            int mouseX = e.getX();
            int mouseY = e.getY();
            // Get the row and column clicked
            int rowSelected = mouseY / CELL_SIZE;
            int colSelected = mouseX / CELL_SIZE;

            if (currentState == GameState.PLAYING) {
               if (rowSelected >= 0 && rowSelected < ROWS && colSelected >= 0
                       && colSelected < COLS && board[rowSelected][colSelected] == Seed.EMPTY) {
                  //Lưu dòng cột đã chọn của người chơi
                  rowPreSelected = rowSelected;
                  colPreSelected = colSelected;

                  board[rowSelected][colSelected] = currentPlayer; // Make a move
                  updateGame(currentPlayer, rowSelected, colSelected); // update state
                  // Switch player
                  currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                  STEPS++;
                  if (currentPlayer == Seed.NOUGHT && currentState == GameState.PLAYING) {
                     if(GameBot == Bot.EASY_BOT) {
                        EasyBot botRun = new EasyBot();
                        String run = botRun.getPosFrBrd(board);
                        String[] splStr = run.split(" ");
                        rowSelected = Integer.parseInt(splStr[0]);
                        colSelected = Integer.parseInt(splStr[1]);
                     }
                     else if(GameBot == Bot.HEURISTIC_BOT){
                        HeuristicBot botRun = new HeuristicBot(ROWS,COLS, Seed.NOUGHT, Seed.CROSS);
                        String run = botRun.getPoint(board);
                        String[] splStr = run.split(" ");
                        rowSelected = Integer.parseInt(splStr[0]);
                        colSelected = Integer.parseInt(splStr[1]);
                     }

                     if (rowSelected >= 0 && rowSelected < ROWS && colSelected >= 0
                             && colSelected < COLS && board[rowSelected][colSelected] == Seed.EMPTY) {
                        //Lưu dòng cột đã chọn của máy
                        rowBotPreSelected = rowSelected;
                        colBotPreSelected = colSelected;

                        board[rowSelected][colSelected] = currentPlayer; // Make a move
                        updateGame(currentPlayer, rowSelected, colSelected); // update state
                        // Switch player
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                     }
                  }
               }
               btnDiLai.setEnabled(true);
               btnBoDiLai.setEnabled(false);
            } else {       // game over
               initGame(); // restart the game
            }
            // Refresh the drawing canvas

            repaint();  // Call-back paintComponent().
         }
      });

      addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            jFrame.setVisible(true);
         }
      });

      // Setup the status bar (JLabel) to display status message
      statusBar = new JLabel("  ");
      statusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
      statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));

      //Thêm Button
      btnDiLai = new Button("Đi lại");
      btnDiLai.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
      btnDiLai.setEnabled(false);
      //btnDiLai.setSize(10,10);

      btnDiLai.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if(currentState != GameState.PLAYING) return;
            if(CheckEmptyBoard()) return;
            rowPreDiLai =rowPreSelected;
            colPreDiLai =colPreSelected;
            colBotPreDiLai =colBotPreSelected;
            rowBotPreDiLai = rowBotPreSelected;
            PlayerReRun = board[rowPreSelected][colPreSelected];
            currentPlayer = PlayerReRun;
            board[rowPreSelected][colPreSelected] = Seed.EMPTY;
            board[rowBotPreSelected][colBotPreSelected] = Seed.EMPTY;
            btnBoDiLai.setEnabled(true);
            btnDiLai.setEnabled(false);
            repaint();
         }
      });

      btnBoDiLai = new Button("Bỏ đi lại");
      btnBoDiLai.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
      btnBoDiLai.setEnabled(false);
      //btnDiLai.setSize(10,10);

      btnBoDiLai.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if(currentState != GameState.PLAYING) return;
            if(STEPS == 0 || PlayerReRun == null ) return;
            board[rowPreDiLai][colPreDiLai] = PlayerReRun;
            board[rowBotPreDiLai][colBotPreDiLai] = Seed.NOUGHT;
            //currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
            btnBoDiLai.setEnabled(false);
            repaint();
         }
      });

      pnButton = new JPanel();
      pnButton.setLayout(new FlowLayout(FlowLayout.CENTER));
      pnButton.add(btnDiLai);
      pnButton.add(btnBoDiLai);


      Container cp = getContentPane();
      cp.setLayout(new BorderLayout());
      cp.add(canvas, BorderLayout.CENTER);
      cp.add(statusBar, BorderLayout.PAGE_END); // same as SOUTH
      cp.add(pnButton, BorderLayout.PAGE_START);

      pack();  // pack all the components in this JFrame
      setTitle("Tic Tac Toe với Máy");
      setLocationRelativeTo(null);
      setVisible(true);  // show this JFrame

      board = new Seed[ROWS][COLS]; // allocate array
      initGame(); // initialize the game board contents and game variables
   }

   // Bot theo thuật toán Heuristic
   public static class HeuristicBot {
      // dong, cot = nxn
      // bot, player = kiểu enum theo platform tictactoe
      // mangTC = điểm TC tính theo số lần xuất hiện của bản thân (bot)
      // mangPT = điểm PT tính theo số lần xuất hiện của người chơi (player)
      // board = mang kiểu enum theo platform
      //
      static int dong, cot;
      static Seed bot;
      static Seed player; // kiểu của bot, người chơi, test với int, có thể thay lại thành enum
      static int[] mangTC = new int[]{0, 10, 600, 3500, 40000000, 70000, 1000000};
      static int[] mangPN = new int[]{0, 7, 700, 4000, 10000, 67000, 500000};
      static long MAX_INT = 100000000;
      // int dongg, int cott, enum bott, enum playerr
      public HeuristicBot(int dongg, int cott, Seed Bott, Seed Playerr){
         dong = dongg;
         cot = cott;
         bot = Bott;
         player = Playerr;
      }

      // Main Clas, get Point when user check in board
      public static String getPoint(Seed[][] board){
         long checkTC = 0;
         long checkPT = 0;
         long max = 0;
         String vTri = new String();
         List<String> list = new ArrayList<>();
         System.out.println();
         for (int i = 0; i < dong; i++){
            for (int ii = 0; ii < cot; ii++)
            {
               if (board[i][ii] == Seed.EMPTY) {
                  checkTC = CheckDoc(i, ii, board, bot) + CheckNgang(i, ii, board, bot) + CheckCheoPhai(i, ii, board, bot) + CheckCheoTrai(i, ii, board, bot);
                  checkPT = PTDoc(i, ii, board, player) + PTNgang(i, ii, board, player) + PTPhai(i, ii, board, player) + PTTrai(i, ii, board, player);
                  long tmp = checkPT + checkTC;
                  if (tmp > max) {
                     list = new ArrayList<String>();
                     max = tmp;
                     vTri = i + " " + ii;
                     list.add(vTri);
                  }
                  if (tmp == max) {
                     vTri = i + " " + ii;
                     list.add(vTri);
                  }
               }
            }
         }
         Random rand = new Random();
         String s = list.get(rand.nextInt(list.size()));

         return s;
      }
      //Tấn công
      // int pos => vị trí cột hiện tại, int rowNow => dòng hiện tại
      public static long CheckNgang(int rowNow, int pos, Seed[][] board, Seed type){
         int ta = 0; int count = 0; int dich = 0;
         boolean flag = false;
         for (int ii = pos+1; ii < cot; ii++){
            if (board[rowNow][ii] == type) ta++;
            else if (board[rowNow][ii] == Seed.EMPTY && flag == false){
               count++;
               flag = true;
               break;
            }
            else if (board[rowNow][ii] == player) {dich ++;break;}
            else {break;}
         }
         flag = false;
         for (int ii = pos-1; ii >= 0; ii--){
            if (board[rowNow][ii] == type) ta++;
            else if (board[rowNow][ii] == Seed.EMPTY && flag == false){
               count++;
               flag = true;
               break;
            }
            else if (board[rowNow][ii] == player) {dich ++;break;}
            else {break;}
         }
         if (ta == 0) return 0;
         if (ta == 3 && dich == 2 && (count == 0 || count == 1)) return 0;
         if (ta == 2 && dich == 2 && (count == 0 || count == 1 || count == 2)) return 0;
         if (ta <= 3 && dich == 1 && count == 0) return 0;
         if (ta == 3 && (dich == 0 || dich == 1)) return MAX_INT/250;
         if (ta >= 4) return MAX_INT;
         return (mangTC[ta]*3)/2 - count*100;
      }
      // colNow => cột hiện tại, pos => vị trí dòng hiện tại
      // int type => enum type
      public static long CheckDoc(int pos, int colNow, Seed[][] board, Seed type){
         int ta = 0; int count = 0; int dich = 0;
         boolean flag = false;
         for (int i = pos+1; i < dong; i++){
            if (board[i][colNow] == type) ta++;
            else if (board[i][colNow] == Seed.EMPTY && flag == false){
               count++;
               flag = true;
               break;
            }
            else if (board[i][colNow] == player) {dich ++;break;}
            else {break;}
         }
         flag = false;
         for (int i = pos-1; i >= 0; i--){
            if (board[i][colNow] == type) ta++;
            else if (board[i][colNow] == Seed.EMPTY && flag == false){ count++;flag = true; break;}
            else if (board[i][colNow] == player) {dich ++;break;}
            else {break;}
         }
         if (ta == 0) return 0;
         if (ta == 3 && dich == 2 && (count == 0 || count == 1)) return 0;
         if (ta == 2 && dich == 2 && (count == 0 || count == 1 || count == 2)) return 0;
         if (ta <= 3 && dich == 1 && count == 0) return 0;
         if (ta == 3 && (dich == 0 || dich == 1)) return MAX_INT/250;
         if (ta >= 4) return MAX_INT;
         return (mangTC[ta]*3)/2 - count*100;
      }
      //pos_row => dòng hiện tại, pos_col => cột hiện tại
      public static long CheckCheoPhai(int pos_row, int pos_col, Seed[][] board, Seed type){
         int ta = 0; int count = 0; int dich = 0;
         boolean flag = false;
         int i = pos_row; int ii = pos_col;
         //check xuống
         while (i+1< dong && ii+1 <cot){
            if (board[i+1][ii+1] == type) ta++;
            else if (board[i+1][ii+1] == Seed.EMPTY && flag == false){
               count++;
               flag = true;
               break;
            }
            else if (board[i+1][ii+1] == player) {dich ++;break;}
            else {break;}
            i = i + 1;
            ii = ii + 1;
         }
         i = pos_row; ii = pos_col;
         flag = false;
         //check lên
         while (i-1 >= 0 && ii-1 >= 0){
            if (board[i-1][ii-1] == type) ta++;
            else if (board[i-1][ii-1] == Seed.EMPTY && flag == false) {
               count++;
               flag = true;
               break;
            }
            else if (board[i-1][ii-1] == player) {dich ++;break;}
            else {break;}
            i = i - 1;
            ii = ii -1;
         }
         if (ta == 0) return 0;
         if (ta == 3 && dich == 2 && (count == 0 || count == 1)) return 0;
         if (ta == 2 && dich == 2 && (count == 0 || count == 1 || count == 2)) return 0;
         if (ta <= 3 && dich == 1 && count == 0) return 0;
         if (ta == 3 && (dich == 0 || dich == 1)) return MAX_INT/250;
         if (ta >= 4) return MAX_INT;
         return (mangTC[ta]*3) - count*100;
      }
      // pos_row => dòng hiện tại, pos_col => cột hiện tại
      public static long CheckCheoTrai(int pos_row, int pos_col, Seed[][] board, Seed type){
         int ta = 0; int count = 0; int dich = 0;
         boolean flag = false;
         int i = pos_row; int ii = pos_col;
         //check xuống
         while (i+1< dong && ii-1 >= 0){
            if (board[i+1][ii-1] == type) ta++;
            else if (board[i+1][ii-1] == Seed.EMPTY && flag == false) {
               count++;
               flag = true;
               break;
            }
            else if (board[i+1][ii-1] == player) {dich ++;break;}
            else {break;}
            i = i + 1;
            ii = ii - 1;
         }
         flag = false;
         i = pos_row; ii = pos_col;
         //check lên
         while (i-1 >= 0 && ii+1 < cot){
            if (board[i-1][ii+1] == type) ta++;
            else if (board[i-1][ii+1] == Seed.EMPTY && flag == false) {
               count++;
               flag = true;
               break;
            }
            else if (board[i-1][ii+1] == player) {dich ++;break;}
            else {break;}
            i = i - 1;
            ii = ii + 1;
         }
         if (ta == 0) return 0;
         if (ta == 3 && dich == 2 && (count == 0 || count == 1)) return 0;
         if (ta == 2 && dich == 2 && (count == 0 || count == 1 || count == 2)) return 0;
         if (ta <= 3 && dich == 1 && count == 0) return 0;
         if (ta == 3 && (dich == 0 || dich == 1)) return MAX_INT/250;
         if (ta >= 4) return MAX_INT;
         return (mangTC[ta]*3) - count*100;
      }

      //Phòng thủ
      // pos => cột hiện tại đang đứng -> di chuyển từ trái qua phải
      // rowNow => dòng hiện tại đang đứng
      // int type => enum type
      public static long PTNgang(int rowNow, int pos, Seed[][] board, Seed type){
         int ta = 0; int count = 1; int dich = 0;
         for (int ii = pos+1; ii < cot; ii++){
            if (board[rowNow][ii] == type) ta++;
            else if (board[rowNow][ii] == Seed.EMPTY) break;
            else if (board[rowNow][ii] == bot) {dich ++;break;}
            else {break;}
         }
         for (int ii = pos-1; ii >= 0; ii--){
            if (board[rowNow][ii] == type) ta++;
            else if (board[rowNow][ii] == Seed.EMPTY) break;
            else if (board[rowNow][ii] == bot) {dich ++;break;}
            else {break;}
         }
         if (ta == 0) return 0;
         if (ta >= 4) return MAX_INT/2;
         if (ta == 3 && (dich == 1 || dich == 0)) return MAX_INT/1000;
         return (mangPN[ta+1]*6)/4 - count;
      }
      //^ ^ ^ ^ or | | | | [n++][i]
      public static long PTDoc(int pos, int colNow, Seed[][] board, Seed type){
         int ta = 0; int count = 1; int dich = 0;
         for (int i = pos+1; i < dong; i++){
            if (board[i][colNow] == type) ta++;
            else if (board[i][colNow] == Seed.EMPTY) break;
            else if (board[i][colNow] == bot) {dich ++;break;}
            else {break;}
         }
         for (int i = pos-1; i >= 0; i--){
            if (board[i][colNow] == type) ta++;
            else if (board[i][colNow] == Seed.EMPTY) break;
            else if (board[i][colNow] == bot) {dich ++;break;}
            else {break;}
         }
         if (ta == 0) return 0;
         if (ta >= 4) return MAX_INT/2;
         if (ta == 3 && (dich == 1 || dich == 0)) return MAX_INT/1000;
         return (mangPN[ta+1]*6)/4 - count;
      }
      // \ \ \ \ \ \ [n++][n++]
      public static long PTPhai(int pos_row, int pos_col, Seed[][] board, Seed type){
         int ta = 0; int count = 1; int dich = 0;
         int i = pos_row; int ii = pos_col;
         //check xuống
         while (i+1< dong && ii+1 <cot){
            if (board[i+1][ii+1] == type) ta++;
            else if (board[i+1][ii+1] == Seed.EMPTY)break;
            else {dich++;break;}
            i = i + 1;
            ii = ii + 1;
         }
         i = pos_row; ii = pos_col;
         //check lên
         while (i-1 >= 0 && ii-1 >= 0){
            if (board[i-1][ii-1] == type) ta++;
            else if (board[i-1][ii-1] == Seed.EMPTY) break;
            else {dich++;break;}
            i = i - 1;
            ii = ii -1;
         }
         if (ta >= 4) return MAX_INT/2;
         if (ta == 3 && (dich == 1 || dich == 0)) return MAX_INT/1000;
         return (mangPN[ta+1]*6)/4 - count;
      }
      public static long PTTrai(int pos_row, int pos_col, Seed[][] board, Seed type){
         int ta = 0; int count = 1;int dich=0;
         int i = pos_row; int ii = pos_col;
         //check xuống
         while (i+1< dong && ii-1 >= 0){
            if (board[i+1][ii-1] == type) ta++;
            else if (board[i+1][ii-1] == Seed.EMPTY) break;
            else {dich++;break;}
            i = i + 1;
            ii = ii - 1;
         }
         i = pos_row; ii = pos_col;
         //check lên
         while (i-1 >= 0 && ii+1 < cot){
            if (board[i-1][ii+1] == type) ta++;
            else if (board[i-1][ii+1] == Seed.EMPTY) break;
            else {dich++;break;}
            i = i - 1;
            ii = ii + 1;
         }
         if (ta >= 4) return MAX_INT/2;
         if (ta == 3 && (dich == 1 || dich == 0)) return MAX_INT/1000;
         return (mangPN[ta+1]*6)/4 - count;
      }
   }
}