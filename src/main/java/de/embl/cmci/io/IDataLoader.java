/**
 * 
 */
package de.embl.cmci.io;

import java.util.Vector;

import ij.measure.ResultsTable;
import de.embl.cmci.linker.DotLinker;
import de.embl.cmci.linker.DotLinker.StackFrames;
import de.embl.cmci.linker.DotLinker.Trajectory;

/**
 * @author Kota Miura
 * 201403
 */
public interface IDataLoader {
	
	StackFrames[] dataLoader(DotLinker dl);
	
	ResultsTable showTrajectoryTable(Vector<Trajectory> all_traj);
}
