/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ChessGui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import ChessCore.*;
import ChessCore.BoardFile;
import ChessCore.Pieces.Piece;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Stack;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author ahmed
 */
public class ChessBoardGui extends javax.swing.JFrame implements Observer {
    // for the command pattern
    private DoCommand send;
    private ClassicChessGame game = new ClassicChessGame();
    private JButton[][] buttons = new JButton[8][8];
    private Square holdSrcSquare;
    private WhitePawnPromotion whitePawnPromotion;
    private BlackPawnPromotion blackPawnPromotion;
    private Stack<JButton> buttonsStack = new Stack<>();
    private Stack<JButton> flippedButtons = new Stack<>();

    // two stacks two hold the row and the col of the destination button
    private Stack<Integer> btnRowDest = new Stack<>();
    private Stack<Integer> btnColDest = new Stack<>();
    // stack to hold the source button
    private Stack<Square> holdStack = new Stack<>();
    // stack to hold the source and destination icons
    private Stack<ImageIcon> holdDestPieces = new Stack<>();
    private Stack<ImageIcon> holdSrcPieces = new Stack<>();
    // stack for the enpassant move
    private Stack<Integer> enpassantStack = new Stack<>();
    // flag for enpassant
    private boolean isEnpassant = false;

    // flag to flip the board
    private boolean switchh = true;

    /**
     * Creates new form ChessBoardGui
     */
    public ChessBoardGui() {
        initComponents();
        this.initiate();
        this.game.addObserver(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setLayout(new java.awt.GridLayout(8, 8, -2, -2));

        jButton1.setBackground(new java.awt.Color(51, 51, 51));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("undo");
        jButton1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(203, 203, 203)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // undo button
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        ImageIcon blackPawn = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\BlackPawn.png");
        ImageIcon whitePawn = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\WhitePawn.png");
        ImageIcon whiteKing = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\WhiteKing.png");
        ImageIcon whiteRook = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\WhiteRook.png");
        ImageIcon blackKing = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\BlackKing.png");
         ImageIcon blackRook = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\BlackRook.png");
        // undo in the backend
       send = new DoCommand(this.game);
       send.unexecute();

        // get the row and column of the source button
        if(holdStack.empty())
            return;
        Square holdSquare = holdStack.pop();
        int srcbuttonRow = 8 - (holdSquare.getRank().getValue() + 1);
        int srcbuttonCol = holdSquare.getFile().getValue();

        // get the row and col of the destination button
        int destbuttonRow = btnRowDest.pop();
        int destbuttonCol = btnColDest.pop();

        // get the icon
        ImageIcon srcImj = holdSrcPieces.pop();
        ImageIcon destImj = holdDestPieces.pop();

        // undo
        buttons[srcbuttonRow][srcbuttonCol].setIcon(srcImj);
        buttons[destbuttonRow][destbuttonCol].setIcon(destImj);

        if (enpassantStack.pop() == 1) {
            if(compareIcons(srcImj, whitePawn))
            buttons[destbuttonRow + 1][destbuttonCol].setIcon(blackPawn);
            else
            buttons[destbuttonRow - 1][destbuttonCol].setIcon(whitePawn);
        }
        
        // for castling
        int deltaX = Math.abs(srcbuttonCol - destbuttonCol);
        
        if(compareIcons(srcImj, whiteKing) && deltaX == 2 ){
            // white king side castle
           if(destbuttonCol > srcbuttonCol){
              buttons[srcbuttonRow][destbuttonCol - 1].setIcon(null);
              buttons[7][7].setIcon(whiteRook);
           }
           else { // castle white queen side
               buttons[srcbuttonRow][destbuttonCol + 1].setIcon(null);
              buttons[7][0].setIcon(whiteRook);
           
           }
           
    
    }else if (compareIcons(srcImj, blackKing) && deltaX == 2 ){
         // black king side castle
         if(destbuttonCol > srcbuttonCol){
              buttons[srcbuttonRow][destbuttonCol - 1].setIcon(null);
              buttons[0][7].setIcon(blackRook);
           }
         else{ // castle black queen side
             buttons[srcbuttonRow][destbuttonCol + 1].setIcon(null);
              buttons[0][0].setIcon(blackRook);
            
         }
    
    }

        // update the game 
      //  gameStatusForGui();
        //updateGameStatus(;
        flipPanelVertical();
        removeHighlight();
        removeRedHighlight();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChessBoardGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChessBoardGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChessBoardGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChessBoardGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ChessBoardGui().setVisible(true);
            }
        });
    }

    public void initiate() {

        // initialize the board by buttons
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {

                int btnRow = rank;
                int btnCol = file;

                buttons[rank][file] = new JButton();
                buttons[btnRow][btnCol].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        if (buttons[btnRow][btnCol].getBackground().equals(Color.YELLOW)) // if the player clicks on the highlighted square
                        {

                            removeHighlight();
                            removeRedHighlight();
                            // row and col in terms of the game
                            int gameRow = 8 - (btnRow + 1);
                            int gameCol = btnCol;
                            // fot the undo
                            btnRowDest.push(btnRow);
                            btnColDest.push(btnCol);

                            holdDestPieces.push((ImageIcon) buttons[btnRow][btnCol].getIcon());

                            moveToHighlightSquare(gameRow, gameCol, btnRow, btnCol);
                           
                           // gameStatusForGui();
                            
                            flipPanelVertical();

                        } else {  // if the player click on non-highlighted square

                            removeHighlight();
                            // row and col in terms of the game
                            int gameRow = 8 - (btnRow + 1);
                            int gameCol = btnCol;

                            highlightSquares(gameRow, gameCol);

                        }

                    }
                });

                jPanel1.add(buttons[rank][file]);
                if ((rank + file) % 2 == 0) {
                    buttons[rank][file].setBackground(Color.WHITE);
                } else {
                    buttons[rank][file].setBackground(Color.BLACK);
                }
            }
        }

        // get the icons for the buttons
        loadImages();

    }

    public void removeHighlight() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (buttons[i][j].getBackground().equals(Color.YELLOW)) {
                    if ((i + j) % 2 == 0) {
                        buttons[i][j].setBackground(Color.WHITE);
                    } else {
                        buttons[i][j].setBackground(Color.BLACK);
                    }

                }

            }

        }

    }

    public void removeRedHighlight() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (buttons[i][j].getBackground().equals(Color.red)) {
                    if ((i + j) % 2 == 0) {
                        buttons[i][j].setBackground(Color.WHITE);
                    } else {
                        buttons[i][j].setBackground(Color.BLACK);
                    }

                }

            }

        }

    }

    public void loadImages() {

        ImageIcon whiteBishop = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\WhiteBishop.png");
        ImageIcon whiteKing = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\WhiteKing.png");
        ImageIcon whiteKnight = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\WhiteKnight.png");
        ImageIcon whitePawn = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\WhitePawn.png");
        ImageIcon whiteQueen = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\WhiteQueen.png");
        ImageIcon whiteRook = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\WhiteRook.png");

        ImageIcon blackBishop = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\BlackBishop.png");
        ImageIcon blackKing = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\BlackKing.png");
        ImageIcon blackKnight = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\BlackKnight.png");
        ImageIcon blackPawn = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\BlackPawn.png");
        ImageIcon blackQueen = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\BlackQueen.png");
        ImageIcon blackRook = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\BlackRook.png");

        // add black pieces
        buttons[0][0].setIcon(blackRook);
        buttons[0][1].setIcon(blackKnight);
        buttons[0][2].setIcon(blackBishop);
        buttons[0][3].setIcon(blackQueen);
        buttons[0][4].setIcon(blackKing);
        buttons[0][5].setIcon(blackBishop);
        buttons[0][6].setIcon(blackKnight);
        buttons[0][7].setIcon(blackRook);
        // Add white pieces
        buttons[7][0].setIcon(whiteRook);
        buttons[7][1].setIcon(whiteKnight);
        buttons[7][2].setIcon(whiteBishop);
        buttons[7][3].setIcon(whiteQueen);
        buttons[7][4].setIcon(whiteKing);
        buttons[7][5].setIcon(whiteBishop);
        buttons[7][6].setIcon(whiteKnight);
        buttons[7][7].setIcon(whiteRook);
        // add black pawns
        for (int i = 0; i < 8; i++) {
            buttons[1][i].setIcon(blackPawn);
        }
        // add white pawns
        for (int i = 0; i < 8; i++) {
            buttons[6][i].setIcon(whitePawn);
        }

    }

    public void highlightSquares(int gameRow, int gameCol) {
        BoardFile f = BoardFile.fromValue(gameCol);
        BoardRank r = BoardRank.fromValue(gameRow);

        Square srcSquare = new Square(f, r);

        game.getAllValidMovesFromSquare(srcSquare);

        for (Square i : game.getAllValidMovesFromSquare(srcSquare)) {

//                            int holdSrcRow = btnRow;
//                            int holdSrcCol = btnCol;
            holdSrcSquare = srcSquare;

            Square toSquare = i;
            int buttonRank = 8 - (i.getRank().getValue() + 1);
            int buttonFile = i.getFile().getValue();

            buttons[buttonRank][buttonFile].setBackground(Color.yellow);
        }

    }

    public void moveToHighlightSquare(int gameRow, int gameCol, int btnRow, int btnCol) {
        // needed icons for special moves

        ImageIcon whiteKing = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\WhiteKing.png");
        ImageIcon whitePawn = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\WhitePawn.png");
        ImageIcon blackKing = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\BlackKing.png");
        ImageIcon blackPawn = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\BlackPawn.png");

        // translate the destination button positions to game positions
        BoardFile f = BoardFile.fromValue(gameCol);
        BoardRank r = BoardRank.fromValue(gameRow);

        // flag for the pawn promotion move 
        boolean isPromotion = false;
        PawnPromotion p = null;

        // convert from source square rank and file to row and col of buttons
        int srcbuttonRank = 8 - (holdSrcSquare.getRank().getValue() + 1);
        int srcbuttonFile = holdSrcSquare.getFile().getValue();

        // for the undo button
        holdStack.push(holdSrcSquare);

        // push the icon of the source square in the stack
        holdSrcPieces.push((ImageIcon) buttons[srcbuttonRank][srcbuttonFile].getIcon());

        boolean forPassantValidation = ( compareIcons((ImageIcon) buttons[srcbuttonRank][srcbuttonFile].getIcon(), whitePawn) || compareIcons((ImageIcon) buttons[srcbuttonRank][srcbuttonFile].getIcon(), blackPawn) );
        // difference betwwen rows for castling move
        int deltaCol = Math.abs(btnCol - srcbuttonFile);
        int deltaRow = Math.abs(srcbuttonRank - btnRow);

        Square destSquare = new Square(f, r);

        // If white castling
        if (compareIcons((ImageIcon) buttons[srcbuttonRank][srcbuttonFile].getIcon(), whiteKing) && deltaCol == 2) {

            whiteCastling(gameRow, gameCol, btnRow, btnCol);

            // If black castling
        } else if (compareIcons((ImageIcon) buttons[srcbuttonRank][srcbuttonFile].getIcon(), blackKing) && deltaCol == 2) {
            blackCastling(gameRow, gameCol, btnRow, btnCol);
        } // en-passant 
        else if (forPassantValidation && deltaCol == 1 &&(deltaCol + deltaRow) == 2 && buttons[btnRow][btnCol].getIcon() == null) {
           
            isEnpassant = true;
            enPassant(btnRow, btnCol);
        } // white pawn promation 
        else if (compareIcons((ImageIcon) buttons[srcbuttonRank][srcbuttonFile].getIcon(), whitePawn) && btnRow == 0) {
            whitePawnPromotion = new WhitePawnPromotion();
            whitePawnPromotion.setChessGame(game);
            whitePawnPromotion.setButtons(buttons);
            whitePawnPromotion.setBtnRow(btnRow);
            whitePawnPromotion.setBtnCol(btnCol);
            whitePawnPromotion.setSrcSquare(holdSrcSquare);
            whitePawnPromotion.setdestSquare(destSquare);
            whitePawnPromotion.setVisible(true);

        } // black pawn promotion
        else if (compareIcons((ImageIcon) buttons[srcbuttonRank][srcbuttonFile].getIcon(), blackPawn) && btnRow == 7) {
            blackPawnPromotion = new BlackPawnPromotion();
            blackPawnPromotion.setChessGame(game);
            blackPawnPromotion.setButtons(buttons);
            blackPawnPromotion.setBtnRow(btnRow);
            blackPawnPromotion.setBtnCol(btnCol);
            blackPawnPromotion.setSrcSquare(holdSrcSquare);
            blackPawnPromotion.setdestSquare(destSquare);
            blackPawnPromotion.setVisible(true);

        } else {
            // make the move in the gui
            buttons[btnRow][btnCol].setIcon(buttons[srcbuttonRank][srcbuttonFile].getIcon());
            buttons[srcbuttonRank][srcbuttonFile].setIcon(null);

        }

        if (isEnpassant == true) {
           
            enpassantStack.push(1);
           
        } else {
            enpassantStack.push(0);
        }

         isEnpassant = false;
        // make the game move

        send = new DoCommand(this.game,holdSrcSquare, destSquare );
        send.execute();

    }

    public void gameStatusForGui() {

        ImageIcon whiteKing = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\WhiteKing.png");

        ImageIcon blackKing = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\BlackKing.png");

        if (game.getGameStatus() == GameStatus.BLACK_UNDER_CHECK) {

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (buttons[i][j].getIcon() != null && compareIcons((ImageIcon) buttons[i][j].getIcon(), blackKing)) {

                        buttons[i][j].setBackground(Color.red);
                    }
                }
            }
        } else if (game.getGameStatus() == GameStatus.WHITE_UNDER_CHECK) {

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (buttons[i][j].getIcon() != null && compareIcons((ImageIcon) buttons[i][j].getIcon(), whiteKing)) {
                        buttons[i][j].setBackground(Color.red);
                    }
                }
            }
        } else if (game.getGameStatus() == GameStatus.WHITE_WON) {
            JOptionPane.showMessageDialog(null, "White Won by checkmate");
        } else if (game.getGameStatus() == GameStatus.BLACK_WON) {
            JOptionPane.showMessageDialog(null, "Black Won by checkmate");
        }

    }

    public boolean compareIcons(ImageIcon button, ImageIcon piece) {
        JLabel buttonLabel = new JLabel(button);
        JLabel pieceLabel = new JLabel(piece);

        if (buttonLabel.getIcon().toString().equals(pieceLabel.getIcon().toString())) {
            return true;
        } else {
            return false;
        }

    }

    private void whiteCastling(int gameRow, int gameCol, int btnRow, int btnCol) {
        // convert from source square rank and file to row and col of buttons
        int srcbuttonRank = 8 - (holdSrcSquare.getRank().getValue() + 1);
        int srcbuttonFile = holdSrcSquare.getFile().getValue();
        if (btnCol < srcbuttonFile) {
            // move the king
            buttons[btnRow][btnCol].setIcon(buttons[srcbuttonRank][srcbuttonFile].getIcon());
            buttons[srcbuttonRank][srcbuttonFile].setIcon(null);
            // move the bottom left (A1) castle
            buttons[btnRow][btnCol + 1].setIcon(buttons[7][0].getIcon());
            buttons[7][0].setIcon(null);

        } else {
            // move the king
            buttons[btnRow][btnCol].setIcon(buttons[srcbuttonRank][srcbuttonFile].getIcon());
            buttons[srcbuttonRank][srcbuttonFile].setIcon(null);
            // move the bottom left (A1) castle
            buttons[btnRow][btnCol - 1].setIcon(buttons[7][7].getIcon());
            buttons[7][7].setIcon(null);

        }

    }

    private void blackCastling(int gameRow, int gameCol, int btnRow, int btnCol) {

        // convert from source square rank and file to row and col of buttons
        int srcbuttonRank = 8 - (holdSrcSquare.getRank().getValue() + 1);
        int srcbuttonFile = holdSrcSquare.getFile().getValue();
        if (btnCol < srcbuttonFile) {
            // move the king
            buttons[btnRow][btnCol].setIcon(buttons[srcbuttonRank][srcbuttonFile].getIcon());
            buttons[srcbuttonRank][srcbuttonFile].setIcon(null);
            // move the bottom left (A1) castle
            buttons[btnRow][btnCol + 1].setIcon(buttons[0][0].getIcon());
            buttons[0][0].setIcon(null);

        } else {
            // move the king
            buttons[btnRow][btnCol].setIcon(buttons[srcbuttonRank][srcbuttonFile].getIcon());
            buttons[srcbuttonRank][srcbuttonFile].setIcon(null);
            // move the bottom left (A1) castle
            buttons[btnRow][btnCol - 1].setIcon(buttons[0][7].getIcon());
            buttons[0][7].setIcon(null);

        }

    }

    private void enPassant(int btnRow, int btnCol) {
        ImageIcon whitePawn = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\WhitePawn.png");
        ImageIcon blackPawn = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\BlackPawn.png");

        // convert from source square rank and file to row and col of buttons
        int srcbuttonRank = 8 - (holdSrcSquare.getRank().getValue() + 1);
        int srcbuttonFile = holdSrcSquare.getFile().getValue();

        if (compareIcons((ImageIcon) buttons[srcbuttonRank][srcbuttonFile].getIcon(), whitePawn)) {
            // move the white pawn
            buttons[btnRow][btnCol].setIcon(buttons[srcbuttonRank][srcbuttonFile].getIcon());
            buttons[srcbuttonRank][srcbuttonFile].setIcon(null);

            // capture the black pawn
            buttons[btnRow + 1][btnCol].setIcon(null);
        } else {

            // move the white pawn
            buttons[btnRow][btnCol].setIcon(buttons[srcbuttonRank][srcbuttonFile].getIcon());
            buttons[srcbuttonRank][srcbuttonFile].setIcon(null);

            // capture the black pawn
            buttons[btnRow - 1][btnCol].setIcon(null);

        }

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    private void flipPanelVertical() {

        if (switchh == true) {
            // put buttons in a stack
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {

                    buttonsStack.push(buttons[i][j]);
                }
            }

        } else {
            // put buttons in a stack
            for (int i = 7; i >= 0; i--) {
                for (int j = 7; j >= 0; j--) {

                    buttonsStack.push(buttons[i][j]);
                }
            }

        }

        if (switchh == true) {
            // add every 10 button in the stack in another stack 
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    flippedButtons.push(buttonsStack.pop());

                }
                // pop in the panel
                for (int k = 0; k < 8; k++) {
                    jPanel1.add(flippedButtons.pop());

                }

            }
        } else {

            // pop in the panel
            for (int k = 0; k < 64; k++) {
                jPanel1.add(buttonsStack.pop());

            }

        }

        // to switch every side correctly  
        switchh = !switchh;

    }

    @Override
    public void updateGameStatus(GameStatus newState) {
       
        newState = this.game.getGameStatus();
       
        observerGameStatusForGui(newState);
    }
    
     public void observerGameStatusForGui(GameStatus newState) {

        ImageIcon whiteKing = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\WhiteKing.png");

        ImageIcon blackKing = new ImageIcon("D:\\prog2-assignments\\ChessGame\\piecesImj\\BlackKing.png");

        if (newState == GameStatus.BLACK_UNDER_CHECK) {

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (buttons[i][j].getIcon() != null && compareIcons((ImageIcon) buttons[i][j].getIcon(), blackKing)) {

                        buttons[i][j].setBackground(Color.red);
                    }
                }
            }
        } else if (newState == GameStatus.WHITE_UNDER_CHECK) {
           
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (buttons[i][j].getIcon() != null && compareIcons((ImageIcon) buttons[i][j].getIcon(), whiteKing)) {
                        buttons[i][j].setBackground(Color.red);
                    }
                }
            }
        } else if (newState == GameStatus.WHITE_WON) {
            JOptionPane.showMessageDialog(null, "White Won by checkmate");
        } else if (newState == GameStatus.BLACK_WON) {
            JOptionPane.showMessageDialog(null, "Black Won by checkmate");
        }

    }

}
