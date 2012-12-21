package org.bioinfo.infrared.lib.impl.mongo;

import java.util.Properties;

import org.bioinfo.infrared.lib.api.BioPaxDBAdaptor;
import org.bioinfo.infrared.lib.api.CpGIslandDBAdaptor;
import org.bioinfo.infrared.lib.api.CytobandDBAdaptor;
import org.bioinfo.infrared.lib.api.ExonDBAdaptor;
import org.bioinfo.infrared.lib.api.GeneDBAdaptor;
import org.bioinfo.infrared.lib.api.GenomeSequenceDBAdaptor;
import org.bioinfo.infrared.lib.api.GenomicVariantEffectDBAdaptor;
import org.bioinfo.infrared.lib.api.MirnaDBAdaptor;
import org.bioinfo.infrared.lib.api.MutationDBAdaptor;
import org.bioinfo.infrared.lib.api.PathwayDBAdaptor;
import org.bioinfo.infrared.lib.api.ProteinDBAdaptor;
import org.bioinfo.infrared.lib.api.RegulatoryRegionDBAdaptor;
import org.bioinfo.infrared.lib.api.SnpDBAdaptor;
import org.bioinfo.infrared.lib.api.StructuralVariationDBAdaptor;
import org.bioinfo.infrared.lib.api.TfbsDBAdaptor;
import org.bioinfo.infrared.lib.api.TranscriptDBAdaptor;
import org.bioinfo.infrared.lib.api.XRefsDBAdaptor;
import org.bioinfo.infrared.lib.impl.DBAdaptorFactory;
import org.hibernate.SessionFactory;

public class MongoDBAdaptorFactory extends DBAdaptorFactory {

	@Override
	public void setConfiguration(Properties properties) {
		// TODO Auto-generated method stub
	}

	@Override
	public void open(String species, String version) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GeneDBAdaptor getGeneDBAdaptor(String species) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GeneDBAdaptor getGeneDBAdaptor(String species, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranscriptDBAdaptor getTranscriptDBAdaptor(String species) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TranscriptDBAdaptor getTranscriptDBAdaptor(String species,
			String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExonDBAdaptor getExonDBAdaptor(String species) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExonDBAdaptor getExonDBAdaptor(String species, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenomicVariantEffectDBAdaptor getGenomicVariantEffectDBAdaptor(
			String species) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenomicVariantEffectDBAdaptor getGenomicVariantEffectDBAdaptor(
			String species, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProteinDBAdaptor getProteinDBAdaptor(String species) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProteinDBAdaptor getProteinDBAdaptor(String species, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SnpDBAdaptor getSnpDBAdaptor(String species) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SnpDBAdaptor getSnpDBAdaptor(String species, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenomeSequenceDBAdaptor getGenomeSequenceDBAdaptor(String species) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenomeSequenceDBAdaptor getGenomeSequenceDBAdaptor(String species,
			String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GeneDBAdaptor getChromosomeDBAdaptor(String species) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CytobandDBAdaptor getCytobandDBAdaptor(String species) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CytobandDBAdaptor getCytobandDBAdaptor(String species, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XRefsDBAdaptor getXRefDBAdaptor(String species) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XRefsDBAdaptor getXRefDBAdaptor(String species, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TfbsDBAdaptor getTfbsDBAdaptor(String species) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TfbsDBAdaptor getTfbsDBAdaptor(String species, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RegulatoryRegionDBAdaptor getRegulatoryRegionDBAdaptor(String species) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RegulatoryRegionDBAdaptor getRegulatoryRegionDBAdaptor(
			String species, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MirnaDBAdaptor getMirnaDBAdaptor(String species) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MirnaDBAdaptor getMirnaDBAdaptor(String species, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BioPaxDBAdaptor getBioPaxDBAdaptor(String species) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BioPaxDBAdaptor getBioPaxDBAdaptor(String species, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MutationDBAdaptor getMutationDBAdaptor(String species) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MutationDBAdaptor getMutationDBAdaptor(String species, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CpGIslandDBAdaptor getCpGIslandDBAdaptor(String species) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CpGIslandDBAdaptor getCpGIslandDBAdaptor(String species,
			String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StructuralVariationDBAdaptor getStructuralVariationDBAdaptor(
			String species) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StructuralVariationDBAdaptor getStructuralVariationDBAdaptor(
			String species, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PathwayDBAdaptor getPathwayDBAdaptor(String species) {
		return getPathwayDBAdaptor(species, null);
	}

	@Override
	public PathwayDBAdaptor getPathwayDBAdaptor(String species, String version) {
//		String speciesVersionPrefix = getSpeciesVersionPrefix(species,version);
//		if(!sessionFactories.containsKey(speciesVersionPrefix)){
//			SessionFactory sessionFactory  = createCellBaseSessionFactory(speciesVersionPrefix);
//			sessionFactories.put(speciesVersionPrefix, sessionFactory);
//		}
		return (PathwayDBAdaptor) new PathwayMongoDBAdaptor(speciesAlias.get(species), version);
	}

}
