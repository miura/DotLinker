package de.embl.cmci.io;

import ij.measure.ResultsTable;

import java.util.Vector;

import de.embl.cmci.linker.DotLinker;
import de.embl.cmci.linker.DotLinker.Trajectory;

public interface IDataWriter {

	public abstract ResultsTable writeTrajectoryTable(Vector<Trajectory> all_traj, DotLinker dotlinker);
}
