package org.bioinfo.cellbase.lib.api;

import java.util.List;

import org.bioinfo.cellbase.lib.common.Position;
import org.bioinfo.cellbase.lib.common.Region;
import org.bioinfo.cellbase.lib.common.core.Gene;
import org.bioinfo.cellbase.lib.impl.dbquery.QueryOptions;
import org.bioinfo.cellbase.lib.impl.dbquery.QueryResponse;

public interface GeneDBAdaptor extends FeatureDBAdaptor {

	// @Override

	/**
	 * List<Gene>
	 * @param biotype
	 * @param id
	 * @return
	 */
//	public QueryResult getAll(List<String> biotype, Boolean id);
	
	public QueryResponse getAll(QueryOptions options);


    public QueryResponse next(String id, QueryOptions options);

	/**
	 * List<String>
	 * @return
	 */
//	public QueryResult getAllEnsemblIds();

	
//	public QueryResult getByEnsemblId(String ensemblId);
//
//	public List<Gene> getAllByEnsemblIdList(List<String> ensemblIdList);
	
	public QueryResponse getAllById(String id, QueryOptions options);

	public QueryResponse getAllByIdList(List<String> idList, QueryOptions options);

//	public Gene getByEnsemblId(String ensemblId, boolean fetchTranscriptsAndExons);
//
//	public List<Gene> getAllByEnsemblIdList(List<String> ensemblIdList, boolean fetchTranscriptsAndExons);
//
//	public QueryResult getAllByName(String name, List<String> exclude);
//
//	public List<List<Gene>> getAllByNameList(List<String> nameList, List<String> exclude);
//
//	public QueryResult getByEnsemblTranscriptId(String transcriptId);
//
//	public QueryResult getAllByEnsemblTranscriptIdList(List<String> transcriptIdList);

//	public List<Gene> getByXref(String xref, List<String> exclude);
//
//	public List<List<Gene>> getByXrefList(List<String> xrefList, List<String> exclude);

	// Now they are filters of getAll
//	public QueryResult getAllByBiotype(String biotype);
//
//	public List<Gene> getAllByBiotypeList(List<String> biotypeList);

//	public List<Gene> getAllByPosition(String chromosome, int position);
	
	public QueryResponse getAllByPosition(String chromosome, int position, QueryOptions options);

//	public List<Gene> getAllByPosition(Position position);
	
	public QueryResponse getAllByPosition(Position position, QueryOptions options);

//	public List<List<Gene>> getAllByPositionList(List<Position> positionList);
	
	public QueryResponse getAllByPositionList(List<Position> positionList, QueryOptions options);

//	public List<Gene> getAllByRegion(String chromosome);
//
//	public List<Gene> getAllByRegion(String chromosome, int start);
//
//	public QueryResult getAllByRegion(String chromosome, int start, int end, boolean fetchTranscripts);

//	public QueryResult getAllByRegion(String chromosome, int start, int end, List<String> biotypeList, boolean fetchTranscripts);
	
	public QueryResponse getAllByRegion(String chromosome, int start, int end, QueryOptions options);

//	public QueryResult getAllByRegion(Region region, boolean fetchTranscripts);
//
//	public QueryResult getAllByRegion(Region region, List<String> biotypeList, boolean fetchTranscripts);
	
	public QueryResponse getAllByRegion(Region region, QueryOptions options);

//	public QueryResult getAllByRegionList(List<Region> regionList, boolean fetchTranscripts);
//
//	public QueryResult getAllByRegionList(List<Region> regions, List<String> biotypeList, boolean fetchTranscripts);
	
	public QueryResponse getAllByRegionList(List<Region> regions, QueryOptions options);

	public List<Gene> getAllByCytoband(String chromosome, String cytoband);

	public List<Gene> getAllBySnpId(String snpId);

	public List<List<Gene>> getAllBySnpIdList(List<String> snpIdList);

	public List<Gene> getAllByTf(String id);

	public List<List<Gene>> getAllByTfList(List<String> idList);

	public List<Gene> getAllByTfName(String tfName);

	public List<List<Gene>> getAllByTfNameList(List<String> tfNameList);

	public List<Gene> getAllTargetsByTf(String id);

	public List<List<Gene>> getAllTargetsByTfList(List<String> idList);

	public List<Gene> getAllByMiRnaMature(String mirbaseId);

	public List<List<Gene>> getAllByMiRnaMatureList(List<String> mirbaseIds);

	public List<Gene> getAllTargetsByMiRnaMature(String mirbaseId);

	public List<List<Gene>> getAllTargetsByMiRnaMatureList(List<String> mirbaseIds);

	public String getAllIntervalFrequencies(Region region, int interval);
}
