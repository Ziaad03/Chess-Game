/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessGui;
import ChessCore.*;

/**
 *
 * @author ahmed
 */
public class DoCommand implements Command {
    private ClassicChessGame game;
    private Square source;
    private Square destination;
    
    public DoCommand(ClassicChessGame game, Square src, Square dest){
       this.game = game;
       this.source = src;
       this.destination = dest;
              
    }
    public DoCommand(ClassicChessGame game){
    this.game = game;
    }

    @Override
    public void execute() {
        Move move = new Move(source, destination);
      this.game.makeMove(move);
    }

    @Override
    public void unexecute() {
        this.game.retriveMove();
    }
    
}
