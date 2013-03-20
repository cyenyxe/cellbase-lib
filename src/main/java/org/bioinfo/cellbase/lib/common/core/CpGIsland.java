package org.bioinfo.cellbase.lib.common.core;

// Generated Jun 5, 2012 6:41:13 PM by Hibernate Tools 3.4.0.CR1


/**
 * CpGIsland generated by hbm2java
 */
public class CpGIsland implements java.io.Serializable {

	private String chromosome;
	private int start;
	private int end;
	private String name;
	private int length;
	private int cpgNum;
	private int gcNum;
	private double perCpG;
	private double perGc;
	private double observedExpectedRatio;

	public CpGIsland() {
	}

	public CpGIsland(String chromosome, int start, int end,
			String name, int length, int cpgNum, int gcNum, double perCpG,
			double perGc, double observedExpectedRatio) {
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
		this.name = name;
		this.length = length;
		this.cpgNum = cpgNum;
		this.gcNum = gcNum;
		this.perCpG = perCpG;
		this.perGc = perGc;
		this.observedExpectedRatio = observedExpectedRatio;
	}

	public String getChromosome() {
		return this.chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public int getStart() {
		return this.start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return this.end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLength() {
		return this.length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getCpgNum() {
		return this.cpgNum;
	}

	public void setCpgNum(int cpgNum) {
		this.cpgNum = cpgNum;
	}

	public int getGcNum() {
		return this.gcNum;
	}

	public void setGcNum(int gcNum) {
		this.gcNum = gcNum;
	}

	public double getPerCpG() {
		return this.perCpG;
	}

	public void setPerCpG(double perCpG) {
		this.perCpG = perCpG;
	}

	public double getPerGc() {
		return this.perGc;
	}

	public void setPerGc(double perGc) {
		this.perGc = perGc;
	}

	public double getObservedExpectedRatio() {
		return this.observedExpectedRatio;
	}

	public void setObservedExpectedRatio(double observedExpectedRatio) {
		this.observedExpectedRatio = observedExpectedRatio;
	}

}