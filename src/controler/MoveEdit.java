package controler;


import javax.swing.undo.AbstractUndoableEdit;

import model.Move;

public class MoveEdit extends AbstractUndoableEdit {
	
	private static final long serialVersionUID = 1L;
	Move move;
	// Construction
	public MoveEdit(Move move) { 
	    this.move = move; 
	}
	
	// Annuler la modification
	public void undo() {
	    super.undo();
	    move.undo();
	}
	
	// Refaire la modification
	public void redo() {
	    super.redo();
	    move.doit();
	}
 }