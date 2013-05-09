package org.bioinfo.cellbase.lib.common;

import java.util.ArrayList;
import java.util.List;


public class Region {

	private String chromosome;
	private int start;
	private int end;

	public Region(String chromosome, int start, int end) {
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
	}

	public static Region parseRegion(String regionString) {
		Region region = null;
		if(regionString != null && !regionString.equals("")) {
			if(regionString.indexOf(':') != -1) {
				String[] fields = regionString.split("[:-]", -1);
				if(fields.length == 3) {
					region = new Region(fields[0], Integer.parseInt(fields[1]), Integer.parseInt(fields[2]));
				}
			}else {
				region = new Region(regionString, 0, Integer.MAX_VALUE);
			}
		}
		return region;
	}

	public static List<Region> parseRegions(String regionsString) {
		List<Region> regions = null;
		if(regionsString != null && !regionsString.equals("")) {
			String[] regionItems = regionsString.split(",");
			regions = new ArrayList<Region>(regionItems.length);
			String[] fields;
			for(String regionString: regionItems) {
				if(regionString.indexOf(':') != -1) {
					fields = regionString.split("[:-]", -1);
					if(fields.length == 3) {
						regions.add(new Region(fields[0], Integer.parseInt(fields[1]), Integer.parseInt(fields[2])));
					}else {
						regions.add(null);
					}
				}else {
					regions.add(new Region(regionString, 0, Integer.MAX_VALUE));
				}
			}	
		}
		return regions;
	}

	/**
	 * 
	 * @param regions
	 * @return A comma separated string with all the regions. If parameter is null then a null objects is returned, an empty string is returned if parameter size list is 0 
	 */
	public static String parseRegionList(List<Region> regions) {
		if(regions == null) {
			return null;
		}else {
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<regions.size()-1; i++) {
				if(regions.get(i) != null) {
					sb.append(regions.get(i).toString()).append(",");					
				}else {
					sb.append("null,");
				}
			}
			if(regions.get(regions.size()-1) != null) {
				sb.append(regions.get(regions.size()-1).toString());					
			}else {
				sb.append("null");
			}
			
			return sb.toString();
		}
	}


	@Override
	public String toString() {
		return chromosome+":"+start+"-"+end; 
	}


	/**
	 * @return the chromosome
	 */
	public String getChromosome() {
		return chromosome;
	}

	/**
	 * @param chromosome the chromosome to set
	 */
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}


	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}


	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}

}
