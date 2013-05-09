package org.bioinfo.infrared.lib.common;

import java.io.Serializable;

public class Exon implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6453125614383801773L;
	private String stableId;
	private String chromosome;
	private int start;
	private int end;
	private String strand;
	private int genomicCodingStart;
	private int genomicCodingEnd;
	private int cdnaCodingStart;
	private int cdnaCodingEnd;
	private int cdsStart;
	private int cdsEnd;
	private int phase;
	private int exonNumber;
	
	public Exon() {
		
	}

	public Exon(String stableId, String chromosome, Integer start, Integer end, String strand, Integer genomicCodingStart, Integer genomicCodingEnd, Integer cdnaCodingStart, Integer cdnaCodingEnd, Integer cdsStart, Integer cdsEnd, Integer phase, Integer exonNumber) {
		super();
		this.stableId = stableId;
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
		this.strand = strand;
		this.genomicCodingStart = genomicCodingStart;
		this.genomicCodingEnd = genomicCodingEnd;
		this.cdnaCodingStart = cdnaCodingStart;
		this.cdnaCodingEnd = cdnaCodingEnd;
		this.cdsStart = cdsStart;
		this.cdsEnd = cdsEnd;
		this.phase = phase;
		this.exonNumber = exonNumber;
	}

	public String getStableId() {
		return stableId;
	}

	public void setStableId(String stableId) {
		this.stableId = stableId;
	}

	public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getStrand() {
		return strand;
	}

	public void setStrand(String strand) {
		this.strand = strand;
	}

	public int getExonNumber() {
		return exonNumber;
	}

	public void setExonNumber(int exonNumber) {
		this.exonNumber = exonNumber;
	}

	public int getPhase() {
		return phase;
	}

	public void setPhase(int phase) {
		this.phase = phase;
	}

	public int getGenomicCodingStart() {
		return genomicCodingStart;
	}

	public void setGenomicCodingStart(int codingRegionStart) {
		this.genomicCodingStart = codingRegionStart;
	}

	public int getGenomicCodingEnd() {
		return genomicCodingEnd;
	}

	public void setGenomicCodingEnd(int codingRegionEnd) {
		this.genomicCodingEnd = codingRegionEnd;
	}

	public int getCdnaCodingStart() {
		return cdnaCodingStart;
	}

	public void setCdnaCodingStart(int cdnaCodingStart) {
		this.cdnaCodingStart = cdnaCodingStart;
	}

	public int getCdnaCodingEnd() {
		return cdnaCodingEnd;
	}

	public void setCdnaCodingEnd(int cdnaCodingEnd) {
		this.cdnaCodingEnd = cdnaCodingEnd;
	}

	public int getCdsStart() {
		return cdsStart;
	}

	public void setCdsStart(int cdsStart) {
		this.cdsStart = cdsStart;
	}

	public int getCdsEnd() {
		return cdsEnd;
	}

	public void setCdsEnd(int cdsEnd) {
		this.cdsEnd = cdsEnd;
	}
	
	
}
