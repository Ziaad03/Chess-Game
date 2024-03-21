package ChessCore;

import ChessCore.Pieces.*;
import ChessGui.Observer;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class ChessGame implements Observable  {

    private Observer observer;
    private ChessGameCareTaker careTaker = new ChessGameCareTaker();
    private final ChessBoard board;
    private GameStatus gameStatus = GameStatus.IN_PROGRESS;
    private Player whoseTurn = Player.WHITE;
    private Stack<Move> allMoves = new Stack<>();
    private Stack<Piece> holdDestPieces = new Stack<>();
    private Stack<Piece> holdSrcPieces = new Stack<>();
    private Stack<Integer> enPassantStack = new Stack<>();
    private boolean isEnPasaant = false;
    private Stack<GameStatus> gameStatusStack = new Stack<>();

    private Move lastMove;
    private boolean canWhiteCastleKingSide = true;
    private boolean canWhiteCastleQueenSide = true;
    private boolean canBlackCastleKingSide = true;
    private boolean canBlackCastleQueenSide = true;

    private int whiteKingMoves = 0;
    private int blackKingMoves = 0;
    private int whiteQueenRook = 0;
    private int whiteKingRook = 0;
    private int blackQueenRook = 0;
    private int blackKingRook = 0;

    protected ChessGame(BoardInitializer boardInitializer) {
        this.board = new ChessBoard(boardInitializer.initialize());
    }

    public boolean isCanWhiteCastleKingSide() {
        return canWhiteCastleKingSide;
    }

    public boolean isCanWhiteCastleQueenSide() {
        return canWhiteCastleQueenSide;
    }

    public boolean isCanBlackCastleKingSide() {
        return canBlackCastleKingSide;
    }

    public boolean isCanBlackCastleQueenSide() {
        return canBlackCastleQueenSide;
    }

    protected boolean isValidMove(Move move) {
        if (isGameEnded()) {
            return false;
        }

        Piece pieceAtFrom = board.getPieceAtSquare(move.getFromSquare());
        if (pieceAtFrom == null || pieceAtFrom.getOwner() != whoseTurn || !pieceAtFrom.isValidMove(move, this)) {
            return false;
        }

        Piece pieceAtTo = board.getPieceAtSquare(move.getToSquare());
        // A player can't capture his own piece.
        if (pieceAtTo != null && pieceAtTo.getOwner() == whoseTurn) {
            return false;
        }

        return isValidMoveCore(move);
    }

    public Move getLastMove() {
        return lastMove;
    }

    public Player getWhoseTurn() {
        return whoseTurn;
    }

    ChessBoard getBoard() {
        return board;
    }

    protected abstract boolean isValidMoveCore(Move move);

    public boolean isTherePieceInBetween(Move move) {
        return board.isTherePieceInBetween(move);
    }

    public boolean hasPieceIn(Square square) {
        return board.getPieceAtSquare(square) != null;
    }

    public boolean hasPieceInSquareForPlayer(Square square, Player player) {
        Piece piece = board.getPieceAtSquare(square);
        return piece != null && piece.getOwner() == player;
    }

    public boolean makeMove(Move move) {
        if (!isValidMove(move)) {
            return false;
        }

        // store move
        careTaker.SaveMove(move);
     
        
        // store the piece of the destination square
        Piece toPiece = board.getPieceAtSquare(move.getToSquare());
        holdDestPieces.push(toPiece);

        Square fromSquare = move.getFromSquare();
        Piece fromPiece = board.getPieceAtSquare(fromSquare);
        // store the piece of the source square
        holdSrcPieces.push(fromPiece);
        
        // track the moves of the king and the rook
        trackKingAndRookMoves(fromPiece, fromSquare);

        // If the king has moved, castle is not allowed.
        if (fromPiece instanceof King) {

            if (fromPiece.getOwner() == Player.WHITE) {
                canWhiteCastleKingSide = false;
                canWhiteCastleQueenSide = false;
            } else {
                canBlackCastleKingSide = false;
                canBlackCastleQueenSide = false;
            }
        }

        // If the rook has moved, castle is not allowed on that specific side..
        if (fromPiece instanceof Rook) {
            if (fromPiece.getOwner() == Player.WHITE) {
                if (fromSquare.getFile() == BoardFile.A && fromSquare.getRank() == BoardRank.FIRST) {
                    canWhiteCastleQueenSide = false;
                } else if (fromSquare.getFile() == BoardFile.H && fromSquare.getRank() == BoardRank.FIRST) {
                    canWhiteCastleKingSide = false;
                }
            } else {
                if (fromSquare.getFile() == BoardFile.A && fromSquare.getRank() == BoardRank.EIGHTH) {
                    canBlackCastleQueenSide = false;
                } else if (fromSquare.getFile() == BoardFile.H && fromSquare.getRank() == BoardRank.EIGHTH) {
                    canBlackCastleKingSide = false;
                }
            }
        }

        // En-passant.
        if (fromPiece instanceof Pawn
                && move.getAbsDeltaX() == 1
                && !hasPieceIn(move.getToSquare())) {
            board.setPieceAtSquare(lastMove.getToSquare(), null);
            isEnPasaant = true;
        }
        // if it is enpassant move push 1 else push 0
        if (isEnPasaant == true) {
            enPassantStack.push(1);
            isEnPasaant = false;

        } else {
            enPassantStack.push(0);
        }

        // Promotion
        if (fromPiece instanceof Pawn) {
            BoardRank toSquareRank = move.getToSquare().getRank();
            if (toSquareRank == BoardRank.FIRST || toSquareRank == BoardRank.EIGHTH) {
                Player playerPromoting = toSquareRank == BoardRank.EIGHTH ? Player.WHITE : Player.BLACK;
                PawnPromotion promotion = move.getPawnPromotion();
                switch (promotion) {
                    case Queen:
                        fromPiece = new Queen(playerPromoting);
                        break;
                    case Rook:
                        fromPiece = new Rook(playerPromoting);
                        break;
                    case Knight:
                        fromPiece = new Knight(playerPromoting);
                        break;
                    case Bishop:
                        fromPiece = new Bishop(playerPromoting);
                        break;
                    case None:
                        throw new RuntimeException("Pawn moving to last rank without promotion being set. This should NEVER happen!");
                }
            }
        }

        // Castle
        if (fromPiece instanceof King
                && move.getAbsDeltaX() == 2) {

            Square toSquare = move.getToSquare();
            if (toSquare.getFile() == BoardFile.G && toSquare.getRank() == BoardRank.FIRST) {
                // White king-side castle.
                // Rook moves from H1 to F1
                Square h1 = new Square(BoardFile.H, BoardRank.FIRST);
                Square f1 = new Square(BoardFile.F, BoardRank.FIRST);
                Piece rook = board.getPieceAtSquare(h1);
                board.setPieceAtSquare(h1, null);
                board.setPieceAtSquare(f1, rook);
            } else if (toSquare.getFile() == BoardFile.G && toSquare.getRank() == BoardRank.EIGHTH) {
                // Black king-side castle.
                // Rook moves from H8 to F8
                Square h8 = new Square(BoardFile.H, BoardRank.EIGHTH);
                Square f8 = new Square(BoardFile.F, BoardRank.EIGHTH);
                Piece rook = board.getPieceAtSquare(h8);
                board.setPieceAtSquare(h8, null);
                board.setPieceAtSquare(f8, rook);
            } else if (toSquare.getFile() == BoardFile.C && toSquare.getRank() == BoardRank.FIRST) {
                // White queen-side castle.
                // Rook moves from A1 to D1
                Square a1 = new Square(BoardFile.A, BoardRank.FIRST);
                Square d1 = new Square(BoardFile.D, BoardRank.FIRST);
                Piece rook = board.getPieceAtSquare(a1);
                board.setPieceAtSquare(a1, null);
                board.setPieceAtSquare(d1, rook);
            } else if (toSquare.getFile() == BoardFile.C && toSquare.getRank() == BoardRank.EIGHTH) {
                // Black queen-side castle.
                // Rook moves from A8 to D8
                Square a8 = new Square(BoardFile.A, BoardRank.EIGHTH);
                Square d8 = new Square(BoardFile.D, BoardRank.EIGHTH);
                Piece rook = board.getPieceAtSquare(a8);
                board.setPieceAtSquare(a8, null);
                board.setPieceAtSquare(d8, rook);
            }
        }

        board.setPieceAtSquare(fromSquare, null);
        board.setPieceAtSquare(move.getToSquare(), fromPiece);

        whoseTurn = Utilities.revertPlayer(whoseTurn);
        lastMove = move;
        updateGameStatus();
        // observer.notify
        notifyObserver();

        return true;
    }

    private void updateGameStatus() {
        Player whoseTurn = getWhoseTurn();
        boolean isInCheck = Utilities.isInCheck(whoseTurn, getBoard());
        boolean hasAnyValidMoves = hasAnyValidMoves();
        if (isInCheck) {
            if (!hasAnyValidMoves && whoseTurn == Player.WHITE) {
                gameStatus = GameStatus.BLACK_WON;
            } else if (!hasAnyValidMoves && whoseTurn == Player.BLACK) {
                gameStatus = GameStatus.WHITE_WON;
            } else if (whoseTurn == Player.WHITE) {
                gameStatus = GameStatus.WHITE_UNDER_CHECK;
            } else {
                gameStatus = GameStatus.BLACK_UNDER_CHECK;
            }
        } else if (!hasAnyValidMoves) {
            gameStatus = GameStatus.STALEMATE;
        } else {
            gameStatus = GameStatus.IN_PROGRESS;
        }

        // Note: Insufficient material can happen while a player is in check. Consider this scenario:
        // Board with two kings and a lone pawn. The pawn is promoted to a Knight with a check.
        // In this game, a player will be in check but the game also ends as insufficient material.
        // For this case, we just mark the game as insufficient material.
        // It might be better to use some sort of a "Flags" enum.
        // Or, alternatively, don't represent "check" in gameStatus
        // Instead, have a separate isWhiteInCheck/isBlackInCheck methods.
        if (isInsufficientMaterial()) {
            gameStatus = GameStatus.INSUFFICIENT_MATERIAL;
        }
        // save the state of the game
        gameStatusStack.push(gameStatus);

    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public boolean isGameEnded() {
        return gameStatus == GameStatus.WHITE_WON
                || gameStatus == GameStatus.BLACK_WON
                || gameStatus == GameStatus.STALEMATE
                || gameStatus == GameStatus.INSUFFICIENT_MATERIAL;
    }

    private boolean isInsufficientMaterial() {
        /*
        If both sides have any one of the following, and there are no pawns on the board:

        A lone king
        a king and bishop
        a king and knight
         */
        int whiteBishopCount = 0;
        int blackBishopCount = 0;
        int whiteKnightCount = 0;
        int blackKnightCount = 0;

        for (BoardFile file : BoardFile.values()) {
            for (BoardRank rank : BoardRank.values()) {
                Piece p = getPieceAtSquare(new Square(file, rank));
                if (p == null || p instanceof King) {
                    continue;
                }

                if (p instanceof Bishop) {
                    if (p.getOwner() == Player.WHITE) {
                        whiteBishopCount++;
                    } else {
                        blackBishopCount++;
                    }
                } else if (p instanceof Knight) {
                    if (p.getOwner() == Player.WHITE) {
                        whiteKnightCount++;
                    } else {
                        blackKnightCount++;
                    }
                } else {
                    // There is a non-null piece that is not a King, Knight, or Bishop.
                    // This can't be insufficient material.
                    return false;
                }
            }
        }

        boolean insufficientForWhite = whiteKnightCount + whiteBishopCount <= 1;
        boolean insufficientForBlack = blackKnightCount + blackBishopCount <= 1;
        return insufficientForWhite && insufficientForBlack;
    }

    private boolean hasAnyValidMoves() {
        for (BoardFile file : BoardFile.values()) {
            for (BoardRank rank : BoardRank.values()) {
                if (!getAllValidMovesFromSquare(new Square(file, rank)).isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<Square> getAllValidMovesFromSquare(Square square) {
        ArrayList<Square> validMoves = new ArrayList<>();
        for (var i : BoardFile.values()) {
            for (var j : BoardRank.values()) {
                var sq = new Square(i, j);
                if (isValidMove(new Move(square, sq, PawnPromotion.Queen))) {
                    validMoves.add(sq);
                }
            }
        }

        return validMoves;
    }

    public Piece getPieceAtSquare(Square square) {
        return board.getPieceAtSquare(square);
    }

    public void retriveMove() {

        // pop the last move from the stack
        Move moveRetrived = null;

        if (!careTaker.StackisEmpty()) {
            moveRetrived = careTaker.revertMove();
        } else {
            return;
        }

        // get the from and to squares
        Square srcSquare = moveRetrived.getFromSquare();
        Square destSquare = moveRetrived.getToSquare();

        //get the pieces included in the move
        Piece srcPiece = null;
        Piece destPiece = null;
        srcPiece = holdSrcPieces.pop();
        destPiece = holdDestPieces.pop();
        
        retriveKingAndRookMoves(srcPiece, srcSquare);

        // undo the move
        board.setPieceAtSquare(srcSquare, srcPiece);
        board.setPieceAtSquare(destSquare, destPiece);

        // pop from enpassant stack and if 1 it means this is enpassant move
        if (enPassantStack.pop() == 1) {
            if (srcPiece.getOwner() == Player.WHITE) {

                // enPassant square
                int destFile = destSquare.getFile().getValue();
                int destRank = destSquare.getRank().getValue();
                BoardFile f = BoardFile.fromValue(destFile);
                BoardRank r = BoardRank.fromValue(destRank - 1);
                Square capturedEnPassant = new Square(f, r);

                Piece blackPawn = new Pawn(Player.BLACK);
                board.setPieceAtSquare(capturedEnPassant, blackPawn);

            } else {
                // enPassant square
                int destFile = destSquare.getFile().getValue();
                int destRank = destSquare.getRank().getValue();
                BoardFile f = BoardFile.fromValue(destFile);
                BoardRank r = BoardRank.fromValue(destRank + 1);
                Square capturedEnPassant = new Square(f, r);

                Piece whitePawn = new Pawn(Player.WHITE);
                board.setPieceAtSquare(capturedEnPassant, whitePawn);

            }

        }

        if (srcPiece instanceof King
                && moveRetrived.getAbsDeltaX() == 2) {
            // castle white king side
            if (destSquare.getFile() == BoardFile.G && destSquare.getRank() == BoardRank.FIRST) {
                // determine the source and destintion of the rook
                Piece whiteRook = new Rook(Player.WHITE);
                Square rookSrcSquare = new Square(BoardFile.H, BoardRank.FIRST);
                Square rookDestSquare = new Square(BoardFile.F, BoardRank.FIRST);
                board.setPieceAtSquare(rookDestSquare, null);
                board.setPieceAtSquare(rookSrcSquare, whiteRook);
                canWhiteCastleKingSide = true;

            } //castle black king side
            else if (destSquare.getFile() == BoardFile.G && destSquare.getRank() == BoardRank.EIGHTH) {

                // determine the source and destintion of the rook
                Piece blackRook = new Rook(Player.BLACK);
                Square rookSrcSquare = new Square(BoardFile.H, BoardRank.EIGHTH);
                Square rookDestSquare = new Square(BoardFile.F, BoardRank.EIGHTH);
                board.setPieceAtSquare(rookDestSquare, null);
                board.setPieceAtSquare(rookSrcSquare, blackRook);
                canBlackCastleKingSide = true;

            } // castle white queen side
            else if (destSquare.getFile() == BoardFile.C && destSquare.getRank() == BoardRank.FIRST) {
                Piece whiteRook = new Rook(Player.WHITE);
                Square rookSrcSquare = new Square(BoardFile.A, BoardRank.FIRST);
                Square rookDestSquare = new Square(BoardFile.D, BoardRank.FIRST);
                board.setPieceAtSquare(rookDestSquare, null);
                board.setPieceAtSquare(rookSrcSquare, whiteRook);
                canWhiteCastleQueenSide = true;

            } // castle black queen side
            else if (destSquare.getFile() == BoardFile.C && destSquare.getRank() == BoardRank.EIGHTH) {

                Piece blackRook = new Rook(Player.BLACK);
                Square rookSrcSquare = new Square(BoardFile.A, BoardRank.EIGHTH);
                Square rookDestSquare = new Square(BoardFile.D, BoardRank.EIGHTH);
                board.setPieceAtSquare(rookDestSquare, null);
                board.setPieceAtSquare(rookSrcSquare, blackRook);
                canBlackCastleQueenSide = true;

            }

        }

        // update the game
        whoseTurn = Utilities.revertPlayer(whoseTurn);
        //  updateGameStatus();
        gameStatusStack.pop();
        if (!gameStatusStack.empty()) {
            gameStatus = gameStatusStack.peek();
        }
        if (!careTaker.StackisEmpty()) {
            lastMove = careTaker.getlastMove();
        }
        
        updateCastling();
        notifyObserver();

    }

    public void trackKingAndRookMoves(Piece piece, Square fromSquare) {

        // if the king move
        if (piece instanceof King) {

            if (piece.getOwner() == Player.WHITE) {
                whiteKingMoves++;} 
            else {
                blackKingMoves++;}
        }
        
         // If the rook has moved
        if (piece instanceof Rook) {
            if (piece.getOwner() == Player.WHITE) {
                if (fromSquare.getFile() == BoardFile.A && fromSquare.getRank() == BoardRank.FIRST) {
                    whiteQueenRook++;
                } else if (fromSquare.getFile() == BoardFile.H && fromSquare.getRank() == BoardRank.FIRST) {
                    whiteKingRook++;
                }
            } else {
                if (fromSquare.getFile() == BoardFile.A && fromSquare.getRank() == BoardRank.EIGHTH) {
                   blackQueenRook++;
                } else if (fromSquare.getFile() == BoardFile.H && fromSquare.getRank() == BoardRank.EIGHTH) {
                    blackKingRook++;
                }
            }
        }
        
        

    }
    
    public void retriveKingAndRookMoves(Piece piece, Square fromSquare){
    
         // if the king move
        if (piece instanceof King) {

            if (piece.getOwner() == Player.WHITE) {
                whiteKingMoves--;} 
            else {
                blackKingMoves--;}
        }
        
          // If the rook has moved
        if (piece instanceof Rook) {
            if (piece.getOwner() == Player.WHITE) {
                if (fromSquare.getFile() == BoardFile.A && fromSquare.getRank() == BoardRank.FIRST) {
                    whiteQueenRook--;
                } else if (fromSquare.getFile() == BoardFile.H && fromSquare.getRank() == BoardRank.FIRST) {
                    whiteKingRook--;
                }
            } else {
                if (fromSquare.getFile() == BoardFile.A && fromSquare.getRank() == BoardRank.EIGHTH) {
                   blackQueenRook--;
                } else if (fromSquare.getFile() == BoardFile.H && fromSquare.getRank() == BoardRank.EIGHTH) {
                    blackKingRook--;
                }
            }
        }
        
    }

    private void updateCastling() {
          if(whiteKingMoves == 0 && whiteKingRook == 0)
              canWhiteCastleKingSide = true;
          if(whiteKingMoves == 0 && whiteQueenRook == 0)
              canWhiteCastleQueenSide = true;
          if(blackKingMoves == 0 && blackKingRook == 0)
              canBlackCastleKingSide = true;
          if(blackKingMoves == 0 && blackQueenRook == 0)
              canBlackCastleQueenSide = true;
                  
        
    }

    @Override
    public void addObserver(Observer o) {
        this.observer = o;
    }

    @Override
    public void removeObserver(Observer o) {
    }

    @Override
    public void notifyObserver() {
        this.observer.updateGameStatus( this.getGameStatus());
    }
    
    
    
}
