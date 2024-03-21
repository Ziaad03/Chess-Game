/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessCore;

import java.util.Stack;

/**
 *
 * @author ahmed
 */
public class ChessGameCareTaker {
   private Stack<Move> saves = new Stack<>();
   
   public void SaveMove(Move move){
      saves.push(move);
   }
   
   public Move revertMove(){
         return saves.pop();
   }
   
   public boolean StackisEmpty(){
      if(saves.empty())
          return true;
      else 
          return false;
   }
   public Move getlastMove(){
        return saves.peek();
   }
}
