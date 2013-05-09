package org.bioinfo.cellbase.lib.api;

import java.util.List;

import org.bioinfo.cellbase.lib.common.IntervalFeatureFrequency;
import org.bioinfo.cellbase.lib.common.Region;
import org.bioinfo.cellbase.lib.common.regulatory.ConservedRegion;
import org.bioinfo.cellbase.lib.common.regulatory.RegulatoryRegion;

public interface RegulatoryRegionDBAdaptor extends FeatureDBAdaptor {

	public List<RegulatoryRegion> getAllByRegion(String chromosome);

	public List<RegulatoryRegion> getAllByRegion(String chromosome, int start);

	public List<RegulatoryRegion> getAllByRegion(String chromosome, int start, int end);

	public List<RegulatoryRegion> getAllByRegion(Region region);

	public List<List<RegulatoryRegion>> getAllByRegionList(List<Region> regionList);
	

	public List<RegulatoryRegion> getAllByRegion(String chromosome, List<String> type);

	public List<RegulatoryRegion> getAllByRegion(String chromosome, int start, List<String> type);

	public List<RegulatoryRegion> getAllByRegion(String chromosome, int start, int end, List<String> type);
	
	public List<RegulatoryRegion> getAllByRegion2(String chromosome, int start, int end, List<String> type);

	public List<RegulatoryRegion> getAllByRegion(Region region, String type);

	public List<List<RegulatoryRegion>> getAllByRegionList(List<Region> regionList, List<String> type);

	public List<RegulatoryRegion> getAllByInternalId(String id);

	public List<RegulatoryRegion> getAllByInternalIdList(List<String> idList);

	public List<String> getAllFeatureMapByRegion(List<Region> region);
	
	
	public List<ConservedRegion> getAllConservedRegionByRegion(Region region);
	
	public List<List<ConservedRegion>> getAllConservedRegionByRegionList(List<Region> regionList);
	
	
	public List<IntervalFeatureFrequency> getAllRegulatoryRegionIntervalFrequencies(Region region, int interval, String type);

	public List<IntervalFeatureFrequency> getAllRegulatoryRegionIntervalFrequencies(Region region, int interval);
	
	public List<IntervalFeatureFrequency> getAllConservedRegionIntervalFrequencies(Region region, int interval);

}
