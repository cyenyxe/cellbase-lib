package org.bioinfo.cellbase.lib.api;

import java.util.List;

import org.bioinfo.cellbase.lib.common.ProteinRegion;
import org.bioinfo.cellbase.lib.common.Region;
import org.bioinfo.cellbase.lib.impl.dbquery.QueryResult;
import org.bioinfo.formats.parser.biopax.ProteinInteraction;
import org.bioinfo.formats.parser.uniprot.v140jaxb.DbReferenceType;
import org.bioinfo.formats.parser.uniprot.v140jaxb.FeatureType;
import org.bioinfo.formats.parser.uniprot.v140jaxb.Protein;
import org.bioinfo.formats.parser.uniprot.v140jaxb.SequenceType;

public interface ProteinDBAdaptor extends FeatureDBAdaptor {

	
	@Override
	public QueryResult getAll();

	public List<String> getAllUniprotAccessions();
	
	public List<String> getAllUniprotNames();

	
	public List<Protein> getAllByUniprotAccession(String uniprotId);

	public List<List<Protein>> getAllByUniprotAccessionList(List<String> uniprotIdList);

	
	public List<Protein> getAllByProteinName(String name);

	public List<List<Protein>> getAllByProteinNameList(List<String> nameList);
	
	
	public List<Protein> getAllByEnsemblGene(String ensemblGene);
	
	public List<List<Protein>> getAllByEnsemblGeneList(List<String> ensemblGeneList);

	public List<Protein> getAllByEnsemblTranscriptId(String transcriptId);
	
	public List<List<Protein>> getAllByEnsemblTranscriptIdList(List<String> transcriptIdList);
	
	public List<Protein> getAllByGeneName(String geneName);
	
	public List<List<Protein>> getAllByGeneNameList(List<String> geneNameList);
	
	
	public List<SequenceType> getAllProteinSequenceByProteinName(String name);
	
	public List<List<SequenceType>> getAllProteinSequenceByProteinNameList(List<String> nameList);
	
	
	public List<FeatureType> getAllProteinFeaturesByUniprotId(String name);

	public List<List<FeatureType>> getAllProteinFeaturesByUniprotIdList(List<String> nameList);
	
	public List<FeatureType> getAllProteinFeaturesByGeneName(String name);

	public List<List<FeatureType>> getAllProteinFeaturesByGeneNameList(List<String> nameList);
	
	public List<FeatureType> getAllProteinFeaturesByProteinXref(String name);

	public List<List<FeatureType>> getAllProteinFeaturesByProteinXrefList(List<String> nameList);
	
	
	public List<ProteinInteraction> getAllProteinInteractionsByProteinName(String name);
	
	public List<ProteinInteraction> getAllProteinInteractionsByProteinName(String name, String source);

	public List<List<ProteinInteraction>> getAllProteinInteractionsByProteinNameList(List<String> nameList);
	
	public List<List<ProteinInteraction>> getAllProteinInteractionsByProteinNameList(List<String> nameList, String source);
	
	
	public List<ProteinRegion> getAllProteinRegionByGenomicRegion(Region region);
	
	public List<List<ProteinRegion>> getAllProteinRegionByGenomicRegionList(List<Region> regionList);
	
	
	public List<DbReferenceType> getAllProteinXrefsByProteinName(String name);

	public List<List<DbReferenceType>> getAllProteinXrefsByProteinNameList(List<String> nameList);
	
	public List<DbReferenceType> getAllProteinXrefsByProteinName(String name, String dbname);

	public List<List<DbReferenceType>> getAllProteinXrefsByProteinNameList(List<String> nameList, String dbname);
	
	public List<DbReferenceType> getAllProteinXrefsByProteinName(String name, List<String> dbname);

	public List<List<DbReferenceType>> getAllProteinXrefsByProteinNameList(List<String> nameList, List<String> dbname);

}
