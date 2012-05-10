package org.bioinfo.infrared.lib.impl.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bioinfo.infrared.core.cellbase.ConsequenceType;
import org.bioinfo.infrared.core.cellbase.FeatureMap;
import org.bioinfo.infrared.core.cellbase.GenomeSequence;
import org.bioinfo.infrared.core.cellbase.Snp;
import org.bioinfo.infrared.lib.api.GenomeSequenceDBAdaptor;
import org.bioinfo.infrared.lib.api.GenomicVariantEffectDBAdaptor;
import org.bioinfo.infrared.lib.api.SnpDBAdaptor;
import org.bioinfo.infrared.lib.common.DNASequenceUtils;
import org.bioinfo.infrared.lib.common.GenomicVariant;
import org.bioinfo.infrared.lib.common.GenomicVariantConsequenceType;
import org.bioinfo.infrared.lib.impl.DBAdaptorFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

public class GenomicVariantEffectHibernateDBAdaptor extends HibernateDBAdaptor implements GenomicVariantEffectDBAdaptor {

	private static int FEATURE_MAP_CHUNK_SIZE = 400;

	/** Options **/
	private boolean showFeatures = false;
	private boolean showVariation = true;
	private boolean showRegulatory = true;
	private boolean showDiseases = true;

	private DBAdaptorFactory dbAdaptorFact;
	private GenomeSequenceDBAdaptor sequenceDbAdaptor;
	private SnpDBAdaptor snpDbAdaptor;

	private static Map<String, ConsequenceType> consequenceTypeMap;

	static {
		consequenceTypeMap = new HashMap<String, ConsequenceType>();
		consequenceTypeMap.put("splice_acceptor", new ConsequenceType(23, "SO:0001574", "splice_acceptor_variant", "primary_transcript", "ESSENTIAL_SPLICE_SITE", 1, "splice-3", "Essential splice site", "In the first 2 or the last 2 basepairs of an intron"));
		consequenceTypeMap.put("splice_donor", new ConsequenceType(20, "SO:0001575", "splice_donor_variant", "primary_transcript", "ESSENTIAL_SPLICE_SITE", 1, "splice-5", "Essential splice site", "In the first 2 or the last 2 basepairs of an intron"));		
		consequenceTypeMap.put("stop_gained", new ConsequenceType(11, "SO:0001587", "stop_gained", "mRNA", "STOP_GAINED", 3, "nonsense", "Stop gained", "In coding sequence, resulting in the gain of a stop codon"));
		consequenceTypeMap.put("stop_lost", new ConsequenceType(19, "SO:0001578", "stop_lost", "mRNA", "STOP_LOST", 4, "", "Stop lost", "In coding sequence, resulting in the loss of a stop codon"));
		consequenceTypeMap.put("non_synonymous_codon", new ConsequenceType(6, "SO:0001583", "non_synonymous_codon", "mRNA", "NON_SYNONYMOUS_CODING", 7, "missense", "Non-synonymous coding", "In coding sequence and results in an amino acid change in the encoded peptide sequence"));
		consequenceTypeMap.put("splice_site", new ConsequenceType(14, "SO:0001630", "splice_region_variant", "primary_transcript", "SPLICE_SITE", 8, "", "Splice site", "1-3 bps into an exon or 3-8 bps into an intron"));
		consequenceTypeMap.put("synonymous_codon", new ConsequenceType(9, "SO:0001588", "synonymous_codon", "mRNA", "SYNONYMOUS_CODING", 10, "cds-synon", "Synonymous coding", "In coding sequence, not resulting in an amino acid change (silent mutation)"));
		consequenceTypeMap.put("exon", new ConsequenceType(24, "SO:0001791", "exon_variant", "mRNA", "CODING_UNKNOWN", 11, "", "Coding unknown", "A sequence variant that changes exon sequence"));
		consequenceTypeMap.put("coding_sequence", new ConsequenceType(24, "SO:0001580", "coding_sequence_variant", "mRNA", "CODING_UNKNOWN", 11, "", "Coding unknown", "In coding sequence with indeterminate effect"));
		consequenceTypeMap.put("5_prime_utr", new ConsequenceType(2, "SO:0001623", "5_prime_UTR_variant", "mRNA", "5PRIME_UTR", 13, "untranslated_5", "5 prime UTR", "In 5 prime untranslated region"));
		consequenceTypeMap.put("3_prime_utr", new ConsequenceType(10, "SO:0001624", "3_prime_UTR_variant", "mRNA", "3PRIME_UTR", 14, "untranslated_3", "3 prime UTR", "In 3 prime untranslated region"));
		consequenceTypeMap.put("intron", new ConsequenceType(26, "SO:0001627", "intron_variant", "primary_transcript", "INTRONIC", 15, "intron", "Intronic", "In intron"));
		consequenceTypeMap.put("nmd_transcript", new ConsequenceType(5, "SO:0001621", "NMD_transcript_variant", "mRNA", "NMD_TRANSCRIPT", 16, "", "NMD transcript", "Located within a transcript predicted to undergo nonsense-mediated decay"));
		consequenceTypeMap.put("nc_transcript", new ConsequenceType(15, "SO:0001619", "nc_transcript_variant", "ncRNA", "WITHIN_NON_CODING_GENE", 17, "", "Within non-coding gene", "Located within a gene that does not code for a protein"));
		consequenceTypeMap.put("upstream", new ConsequenceType(28, "SO:0001635", "5KB_upstream_variant", "transcript", "UPSTREAM", 20, "", "Upstream", "Within 5 kb upstream of the 5 prime end of a transcript"));
		consequenceTypeMap.put("downstream", new ConsequenceType(13, "SO:0001633", "5KB_downstream_variant", "transcript", "DOWNSTREAM", 21, "", "Downstream", "Within 5 kb downstream of the 3 prime end of a transcript"));
		consequenceTypeMap.put("tfbs", new ConsequenceType(1, "SO:0001782", "TF_binding_site_variant", "TF_binding_site", "REGULATORY_REGION", 49, "", "Regulatory region", "A sequence variant located within a transcription factor binding site"));
		consequenceTypeMap.put("regulatory_region", new ConsequenceType(7, "SO:0001566", "regulatory_region_variant", "regulatory_region", "REGULATORY_REGION", 50, "", "Regulatory region", "In regulatory region annotated by Ensembl"));
		consequenceTypeMap.put("dnase1", new ConsequenceType(0, "SO:0000685", "DNAseI_hypersensitive_site", "regulatory_region", "REGULATORY_REGION", 100, "", "Regulatory region", ""));
		consequenceTypeMap.put("polymerase", new ConsequenceType(0, "SO:0001203", "RNA_polymerase_promoter", "regulatory_region", "REGULATORY_REGION", 100, "", "Regulatory region", "A region (DNA) to which RNA polymerase binds, to begin transcription"));
		consequenceTypeMap.put("mirna_target", new ConsequenceType(0, "SO:0000934", "miRNA_target_site", "miRNA_target_site", "REGULATORY_REGION", 100, "", "Regulatory region", "A miRNA target site is a binding site where the molecule is a micro RNA"));
		consequenceTypeMap.put("mirna", new ConsequenceType(0, "SO:0000276", "miRNA", "miRNA", "miRNA", 100, "", "miRNA", "Small, ~22-nt, RNA molecule that is the endogenous transcript of a miRNA gene"));
		consequenceTypeMap.put("lincrna", new ConsequenceType(0, "SO:0001463", "lincRNA", "lincRNA", "lincRNA", 100, "", "lincRNA", "A multiexonic non-coding RNA transcribed by RNA polymerase II"));
		consequenceTypeMap.put("pseudogene", new ConsequenceType(0, "SO:0000336", "pseudogene", "pseudogene", "PSEUDOGENE", 100, "", "Pseudogene", "A sequence that closely resembles a known functional gene, at another locus within a genome, that is non-functional as a consequence of (usually several) mutations that prevent either its transcription or translation (or both)"));
		consequenceTypeMap.put("cpg_island", new ConsequenceType(0, "SO:0000307", "CpG_island", "CpG_island", "CpG_ISLAND", 100, "", "CpG_island", "Regions of a few hundred to a few thousand bases in vertebrate genomes that are relatively GC and CpG rich; they are typically unmethylated and often found near the 5' ends of genes"));
		consequenceTypeMap.put("snp", new ConsequenceType(0, "SO:0000694", "SNP", "SNP", "SNP", 100, "", "SNP", "SNPs are single base pair positions in genomic DNA at which different sequence alternatives exist in normal individuals in some population(s), wherein the least frequent variant has an abundance of 1% or greater"));
		consequenceTypeMap.put("intergenic", new ConsequenceType(17, "SO:0001628", "intergenic_variant", "", "INTERGENIC", 100, "", "Intergenic", "More than 5 kb either upstream or downstream of a transcript"));
	}

	public GenomicVariantEffectHibernateDBAdaptor(SessionFactory sessionFactory) {
		super(sessionFactory);
		dbAdaptorFact = new HibernateDBAdaptorFactory();
		sequenceDbAdaptor = dbAdaptorFact.getGenomeSequenceDBAdaptor(species, version);
		snpDbAdaptor = dbAdaptorFact.getSnpDBAdaptor(species, version);
		//		sequenceDbAdaptor = (GenomeSequenceHibernateDBAdaptor) new GenomeSequenceHibernateDBAdaptor(sessionFactory);
		//		snpDbAdaptor = (SnpDBAdaptor) new SnpHibernateDBAdaptor(sessionFactory);
	}

	public GenomicVariantEffectHibernateDBAdaptor(SessionFactory sessionFactory, String species, String version) {
		super(sessionFactory, species, version);
		dbAdaptorFact = new HibernateDBAdaptorFactory();
		sequenceDbAdaptor = dbAdaptorFact.getGenomeSequenceDBAdaptor(species, version);
		snpDbAdaptor = dbAdaptorFact.getSnpDBAdaptor(species, version);
	}



	@Override
	public List<GenomicVariantConsequenceType> getAllConsequenceTypeByVariantList(List<GenomicVariant> variants) {
		return getAllConsequenceTypeByVariantList(variants, null);
	}

	@Override
	public List<GenomicVariantConsequenceType> getAllConsequenceTypeByVariantList(List<GenomicVariant> variants, Set<String> excludeSet) {
		List<GenomicVariantConsequenceType> consequenceTypeList = new ArrayList<GenomicVariantConsequenceType>();

		for(GenomicVariant variant: variants) {
			consequenceTypeList.addAll(getAllConsequenceTypeByVariant(variant, excludeSet));
		}
		return consequenceTypeList;
	}



	@Override
	public Map<GenomicVariant, List<GenomicVariantConsequenceType>> getConsequenceTypeMap(List<GenomicVariant> variants) {
		// TODO
		Map<GenomicVariant, List<GenomicVariantConsequenceType>> consequences = new LinkedHashMap<GenomicVariant, List<GenomicVariantConsequenceType>>(variants.size());

		for (GenomicVariant variant: variants) {
			consequences.put(variant, getAllConsequenceTypeByVariant(variant));
		}
		return consequences;
	}

	@Override
	public Map<GenomicVariant, List<GenomicVariantConsequenceType>> getConsequenceTypeMap(List<GenomicVariant> variants, Set<String> excludeSet) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<GenomicVariantConsequenceType> getAllConsequenceTypeByVariant(GenomicVariant variant) {
		return getAllConsequenceTypeByVariant(variant, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GenomicVariantConsequenceType> getAllConsequenceTypeByVariant(GenomicVariant variant, Set<String> excludeSet) {

		List<GenomicVariantConsequenceType> genomicVariantConsequenceTypeList = null;
		List<Snp> snps;
		boolean isUTR = false;

		List<FeatureMap> featureMapList = null;
		Criteria criteria = this.openSession().createCriteria(FeatureMap.class);
		if(variant != null) {
			int chunkId = variant.getPosition() / FEATURE_MAP_CHUNK_SIZE;
			//			System.out.println("getAllConsequenceTypeByVariant: "+chunkId+", chromosome: "+variant.getChromosome());
			criteria.add(Restrictions.eq("chunkId", chunkId))
			.add(Restrictions.eq("chromosome", variant.getChromosome()))
			.add(Restrictions.le("start", variant.getPosition()))
			.add(Restrictions.ge("end", variant.getPosition()));
			featureMapList = (List<FeatureMap>) executeAndClose(criteria);
		}

		if(featureMapList != null) {
			genomicVariantConsequenceTypeList = new ArrayList<GenomicVariantConsequenceType>(featureMapList.size());
			for(FeatureMap featureMap: featureMapList) {
				if(featureMap.getFeatureType().equalsIgnoreCase("5_prime_utr") || featureMap.getFeatureType().equalsIgnoreCase("3_prime_utr")) {
					isUTR = true;
					break;
				}
			}

			if(excludeSet == null) {
				excludeSet = new HashSet<String>();
			}

			for(FeatureMap featureMap: featureMapList) {
				//				if(excludeSet != null && excludeSet.contains(consequenceTypeMap.get(featureMap.getFeatureType()).getSoTerm())) {
				//				if(excludeSet != null && excludeSet.contains(consequenceTypeMap.get(featureMap.getFeatureType()).getSoTerm())) {
				//					continue;
				//				}
				//				System.out.println("getAllConsequenceTypeByVariant: "+featureMap.toString());

				//				if(featureMap.getFeatureType().equalsIgnoreCase("gene")) {
				////				genomicVariantConsequenceType.add(new GenomicVariantConsequenceType(chromosome, start, end, id, name, type, biotype, featureChromosome, featureStart, featureEnd, featureStrand, snpId, ancestral, alternative, geneId, transcriptId, geneName, consequenceType, consequenceTypeObo, consequenceTypeDesc, consequenceTypeType, aminoacidChange, codonChange));
				//					continue;
				//				}

				if(featureMap.getFeatureType().equalsIgnoreCase("transcript")) {
					if (featureMap.getBiotype().equalsIgnoreCase("mirna") && !excludeSet.contains(consequenceTypeMap.get("mirna").getSoTerm())){
						genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "mirna"));
					}
					if (featureMap.getBiotype().equalsIgnoreCase("nonsense_mediated_decay") && !excludeSet.contains(consequenceTypeMap.get("nmd_transcript").getSoTerm())){
						genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "nmd_transcript"));
					}
					if (featureMap.getBiotype().equalsIgnoreCase("lincrna") && !excludeSet.contains(consequenceTypeMap.get("lincrna").getSoTerm())){
						genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "lincrna"));
					}
					if (featureMap.getBiotype().equalsIgnoreCase("pseudogene") && !excludeSet.contains(consequenceTypeMap.get("pseudogene").getSoTerm())){
						genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "pseudogene"));
					}
					if (featureMap.getBiotype().equalsIgnoreCase("non_coding") && !excludeSet.contains(consequenceTypeMap.get("nc_transcript").getSoTerm())){
						genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "nc_transcript"));
					}
					continue;
				}

				if(featureMap.getFeatureType().equalsIgnoreCase("intron") && !excludeSet.contains(consequenceTypeMap.get("intron").getSoTerm())) {
					genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "intron"));
					continue;
				}

				if(featureMap.getFeatureType(). equalsIgnoreCase("splice_site") && !excludeSet.contains(consequenceTypeMap.get("splice_site").getSoTerm())) {
					genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "splice_site"));
					continue;
				}

				if(featureMap.getFeatureType(). equalsIgnoreCase("splice_donor") && !excludeSet.contains(consequenceTypeMap.get("splice_donor").getSoTerm())) {
					genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "splice_donor"));
					continue;
				}

				if(featureMap.getFeatureType(). equalsIgnoreCase("splice_acceptor") && !excludeSet.contains(consequenceTypeMap.get("splice_acceptor").getSoTerm())) {
					genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "splice_acceptor"));
					continue;
				}

				if(featureMap.getFeatureType().equalsIgnoreCase("exon") && !excludeSet.contains(consequenceTypeMap.get("exon").getSoTerm())) {
					genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "exon"));

					//					int codonPosition = -1;
					if (!isUTR && featureMap.getBiotype().equalsIgnoreCase("protein_coding")) {
						genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "coding_sequence"));							
						if(!featureMap.getExonPhase().equals("") && !featureMap.getExonPhase().equals("-1")) {
							//							if(featureMap.getStrand().equals("1")) {
							//								codonPosition = (variant.getPosition()-featureMap.getStart()+1+Integer.parseInt(featureMap.getExonPhase()))%3;
							//							}else {
							//								codonPosition = (featureMap.getEnd()-variant.getPosition()+1+Integer.parseInt(featureMap.getExonPhase()))%3;
							//							}
							//							System.out.println("codonposition: "+codonPosition);
							//							if(codonPosition == 0) {
							//								codonPosition = 3;
							//							}
							//							String[] codons =  getSequenceByCodon(variant, featureMap, codonPosition);

							int aaPosition = -1;
							if(featureMap.getStrand().equals("1")) {
								aaPosition = ((variant.getPosition()-featureMap.getStart()+1+featureMap.getExonCdnaCodingStart()-featureMap.getTranscriptCdnaCodingStart())/3)+1;
							}else {
								aaPosition = ((featureMap.getEnd()-variant.getPosition()+1+featureMap.getExonCdnaCodingStart()-featureMap.getTranscriptCdnaCodingStart())/3)+1;
							}

							String[] codons =  getSequenceByCodon(variant, featureMap);
							if(DNASequenceUtils.codonToAminoacidShort.get(codons[0]) != null && DNASequenceUtils.codonToAminoacidShort.get(codons[1]) != null) {
								if(DNASequenceUtils.codonToAminoacidShort.get(codons[0]).equals(DNASequenceUtils.codonToAminoacidShort.get(codons[1]))){
									//								this.addConsequenceType(transcript, "synonymous_codon", "SO:0001588", "In coding sequence, not resulting in an amino acid change (silent mutation)", "consequenceTypeType" );
									genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "synonymous_codon", aaPosition, DNASequenceUtils.codonToAminoacidShort.get(codons[0])+"/"+DNASequenceUtils.codonToAminoacidShort.get(codons[1]), codons[0].replaceAll("U", "T")+"/"+codons[1].replaceAll("U", "T")));
								}else{
									//								this.addConsequenceType(transcript, "non_synonymous_codon", "SO:0001583", "In coding sequence and results in an amino acid change in the encoded peptide sequence", "consequenceTypeType", DNASequenceUtils.codonToAminoacidShort.get(referenceSequence)+"/"+ DNASequenceUtils.codonToAminoacidShort.get(alternative), referenceSequence.replace("U", "T")+"/"+alternative.replace("U", "T")  );
									genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "non_synonymous_codon", aaPosition, DNASequenceUtils.codonToAminoacidShort.get(codons[0])+"/"+DNASequenceUtils.codonToAminoacidShort.get(codons[1]), codons[0].replaceAll("U", "T")+"/"+codons[1].replaceAll("U", "T")));

									if ((!DNASequenceUtils.codonToAminoacidShort.get(codons[0]).toLowerCase().equals("stop"))&& (DNASequenceUtils.codonToAminoacidShort.get(codons[1]).toLowerCase().equals("stop"))){
										//									this.addConsequenceType(transcript, "stop_gained", "SO:0001587", "In coding sequence, resulting in the gain of a stop codon", "consequenceTypeType", DNASequenceUtils.codonToAminoacidShort.get(referenceSequence)+"/"+ DNASequenceUtils.codonToAminoacidShort.get(alternative), referenceSequence.replace("U", "T")+"/"+alternative.replace("U", "T")  );
										genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "stop_gained", aaPosition, DNASequenceUtils.codonToAminoacidShort.get(codons[0])+"/"+DNASequenceUtils.codonToAminoacidShort.get(codons[1]), codons[0].replaceAll("U", "T")+"/"+codons[1].replaceAll("U", "T")));
									}

									if ((DNASequenceUtils.codonToAminoacidShort.get(codons[0]).toLowerCase().equals("stop"))&& (!DNASequenceUtils.codonToAminoacidShort.get(codons[1]).toLowerCase().equals("stop"))){
										//									this.addConsequenceType(transcript, "stop_lost", "SO:0001578", "In coding sequence, resulting in the loss of a stop codon", "consequenceTypeType", DNASequenceUtils.codonToAminoacidShort.get(referenceSequence)+"/"+ DNASequenceUtils.codonToAminoacidShort.get(alternative), referenceSequence.replace("U", "T")+"/"+alternative.replace("U", "T")  );
										genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "stop_lost", aaPosition, DNASequenceUtils.codonToAminoacidShort.get(codons[0])+"/"+DNASequenceUtils.codonToAminoacidShort.get(codons[1]), codons[0].replaceAll("U", "T")+"/"+codons[1].replaceAll("U", "T")));
									}
								}
							}
						}
					}
					continue;
				}

				if(featureMap.getFeatureType().equalsIgnoreCase("regulatory_region") && !excludeSet.contains(consequenceTypeMap.get("regulatory_region").getSoTerm())) {
					genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "regulatory_region"));

					if(featureMap.getFeatureName().equalsIgnoreCase("dnase1") || featureMap.getFeatureName().equalsIgnoreCase("faire")) {
						genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "dnase1"));
					}

					if(featureMap.getFeatureName().equalsIgnoreCase("PolII") || featureMap.getFeatureName().equalsIgnoreCase("PolIII")) {
						genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "polymerase"));
					}

					continue;
				}

				if(featureMap.getFeatureType().equalsIgnoreCase("tfbs") && !excludeSet.contains(consequenceTypeMap.get("tfbs").getSoTerm())) {
					genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "tfbs"));
					continue;
				}

				if(featureMap.getFeatureType().equalsIgnoreCase("mirna_target") && !excludeSet.contains(consequenceTypeMap.get("mirna_target").getSoTerm())) {
					genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "mirna_target"));
					continue;
				}

				if(featureMap.getFeatureType().equalsIgnoreCase("upstream") && !excludeSet.contains(consequenceTypeMap.get("upstream").getSoTerm())) {
					genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "upstream"));
					continue;
				}

				if(featureMap.getFeatureType().equalsIgnoreCase("downstream") && !excludeSet.contains(consequenceTypeMap.get("downstream").getSoTerm())) {
					genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "downstream"));
					continue;
				}

				if(featureMap.getFeatureType().equalsIgnoreCase("5_prime_utr") && !excludeSet.contains(consequenceTypeMap.get("5_prime_utr").getSoTerm())) {
					genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "5_prime_utr"));
					continue;
				}

				if(featureMap.getFeatureType().equalsIgnoreCase("3_prime_utr") && !excludeSet.contains(consequenceTypeMap.get("3_prime_utr").getSoTerm())) {
					genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "3_prime_utr"));
					continue;
				}

				if(featureMap.getFeatureType().equalsIgnoreCase("CpG_island") && !excludeSet.contains(consequenceTypeMap.get("cpg_island").getSoTerm())) {
					genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, "cpg_island"));
					continue;
				}

				if(featureMap.getFeatureType().equalsIgnoreCase("snp") && !excludeSet.contains(consequenceTypeMap.get("snp").getSoTerm())) {
					// special method
					snps = snpDbAdaptor.getAllBySnpId(featureMap.getFeatureName());
					if(snps != null && snps.size() > 0) {
						genomicVariantConsequenceTypeList.add(createGenomicVariantConsequenceType(variant, featureMap, snps.get(0), "snp"));
					}
					continue;
				}

			}
		}else {
			// intergenic!!
			genomicVariantConsequenceTypeList = new ArrayList<GenomicVariantConsequenceType>(1);

		}

		return genomicVariantConsequenceTypeList;
	}

	private GenomicVariantConsequenceType createGenomicVariantConsequenceType(GenomicVariant variant, FeatureMap featureMap, String consequenceType) {
		//		chromosome, start, end, id, name, type, biotype, featureChromosome, featureStart, featureEnd, featureStrand,
		//		snpId, ancestral, alternative, geneId, transcriptId, geneName, consequenceType, consequenceTypeObo, consequenceTypeDesc, consequenceTypeType, aminoacidChange, codonChange));
		GenomicVariantConsequenceType g = new GenomicVariantConsequenceType(variant.getChromosome(), 
				variant.getPosition(),
				variant.getReference(),
				variant.getAlternative(),
				featureMap.getFeatureName(),
				featureMap.getGeneName(),
				featureMap.getFeatureType(),
				featureMap.getBiotype(),
				featureMap.getChromosome(),
				featureMap.getStart(),
				featureMap.getEnd(),
				featureMap.getStrand(),
				"", "", "",		// snp info
				featureMap.getGeneStableId(),
				featureMap.getTranscriptStableId(),
				featureMap.getGeneName(),
				consequenceTypeMap.get(consequenceType).getSoAccession(),
				consequenceTypeMap.get(consequenceType).getSoTerm(),
				consequenceTypeMap.get(consequenceType).getDescription(),
				featureMap.getFeatureCategory(),
				-1,"",""
				);
		return g;
	}

	private GenomicVariantConsequenceType createGenomicVariantConsequenceType(GenomicVariant variant, FeatureMap featureMap, String consequenceType, int aaPosition, String aminoacidChange, String codonChange) {
		//		chromosome, start, end, id, name, type, biotype, featureChromosome, featureStart, featureEnd, featureStrand,
		//		snpId, ancestral, alternative, geneId, transcriptId, geneName, consequenceType, consequenceTypeObo, consequenceTypeDesc, consequenceTypeType, aminoacidChange, codonChange));
		GenomicVariantConsequenceType g = new GenomicVariantConsequenceType(variant.getChromosome(), 
				variant.getPosition(), 
				variant.getReference(),
				variant.getAlternative(),
				featureMap.getFeatureName(),
				featureMap.getGeneName(),
				featureMap.getFeatureType(),
				featureMap.getBiotype(),
				featureMap.getChromosome(),
				featureMap.getStart(),
				featureMap.getEnd(),
				featureMap.getStrand(),
				"", "", "",		// snp info
				featureMap.getGeneStableId(),
				featureMap.getTranscriptStableId(),
				featureMap.getGeneName(),
				consequenceTypeMap.get(consequenceType).getSoAccession(),
				consequenceTypeMap.get(consequenceType).getSoTerm(),
				consequenceTypeMap.get(consequenceType).getDescription(),
				featureMap.getFeatureCategory(),
				aaPosition,
				aminoacidChange,
				codonChange
				);
		return g;
	}

	private GenomicVariantConsequenceType createGenomicVariantConsequenceType(GenomicVariant variant, FeatureMap featureMap, Snp snp, String consequenceType) {
		GenomicVariantConsequenceType g = new GenomicVariantConsequenceType(variant.getChromosome(), 
				variant.getPosition(), 
				variant.getReference(),
				variant.getAlternative(),
				featureMap.getFeatureName(),
				featureMap.getGeneName(),
				featureMap.getFeatureType(),
				featureMap.getBiotype(),
				featureMap.getChromosome(),
				featureMap.getStart(),
				featureMap.getEnd(),
				featureMap.getStrand(),
				snp.getName(),
				snp.getAncestralAllele(),
				snp.getAlleleString(),
				featureMap.getGeneStableId(),
				featureMap.getTranscriptStableId(),
				featureMap.getGeneName(),
				consequenceTypeMap.get(consequenceType).getSoAccession(),
				consequenceTypeMap.get(consequenceType).getSoTerm(),
				consequenceTypeMap.get(consequenceType).getDescription(),
				featureMap.getFeatureCategory(),
				-1,"",""
				);
		return g;
	}

	private String[] getSequenceByCodon(GenomicVariant variant, FeatureMap featureMap) {
		String[] codons = new String[2];
		String alternativeAllele = variant.getAlternative();

		int codonPosition = -1;
		//		GenomeSequenceFeature sequence = null;
		GenomeSequence sequence = null;

		if (featureMap.getStrand().equals("-1")) {
			codonPosition = (featureMap.getEnd()-variant.getPosition()+1+Integer.parseInt(featureMap.getExonPhase()))%3;
			if(codonPosition == 1) {
				sequence = sequenceDbAdaptor.getByRegion(featureMap.getChromosome(), variant.getPosition() - 2, variant.getPosition());
			}

			if(codonPosition == 2) {
				sequence = sequenceDbAdaptor.getByRegion(featureMap.getChromosome(), variant.getPosition() - 1, variant.getPosition() + 1);
			}
			/** Caso del 3 **/
			if(codonPosition == 0) {
				sequence = sequenceDbAdaptor.getByRegion(featureMap.getChromosome(), variant.getPosition(), variant.getPosition() + 2);
				codonPosition = 3;
			}

			sequence.setSequence(sequenceDbAdaptor.getRevComp(sequence.getSequence()));
			alternativeAllele = sequenceDbAdaptor.getRevComp(alternativeAllele);
		}else{
			codonPosition = (variant.getPosition()-featureMap.getStart()+1+Integer.parseInt(featureMap.getExonPhase()))%3;
			if (codonPosition == 1){
				sequence = sequenceDbAdaptor.getByRegion(featureMap.getChromosome(), variant.getPosition(), variant.getPosition() + 2);
			}

			if (codonPosition == 2){
				sequence = sequenceDbAdaptor.getByRegion(featureMap.getChromosome(), variant.getPosition() - 1, variant.getPosition() + 1);
			}
			/** Caso del 3 **/
			if (codonPosition == 0){
				sequence = sequenceDbAdaptor.getByRegion(featureMap.getChromosome(), variant.getPosition() - 2, variant.getPosition());
				codonPosition = 3;
			}

			//			sequence.setSequence(GenomeSequenceHibernateDBAdaptor.getRevComp(sequence.getSequence()));
			//			alternativeAllele = GenomeSequenceHibernateDBAdaptor.getRevComp(alternativeAllele);
		}

		//		sequence.setSequence(GenomeSequenceHibernateDBAdaptor.getRevComp(sequence.getSequence()));
		//		String alternativeAllele = GenomeSequenceHibernateDBAdaptor.getRevComp(alternativeAllele);

		String referenceSequence = sequence.getSequence();

		char[] referenceSequenceCharArray = referenceSequence.toCharArray();
		referenceSequenceCharArray[codonPosition - 1] = alternativeAllele.toCharArray()[0]; 

		String alternative = new String();
		for (int i = 0; i < referenceSequenceCharArray.length; i++) {
			alternative = alternative + referenceSequenceCharArray[i];
		}

		//		referenceSequence = referenceSequence.replaceAll("T", "U");
		//		alternative = alternative.replaceAll("T", "U");
		codons[0] = referenceSequence.replaceAll("T", "U");
		codons[1] = alternative.replaceAll("T", "U");
		//		this.addConsequenceType(transcript, "coding_sequence_variant", "SO:0001580", " In coding sequence with indeterminate effect", "consequenceTypeType" );
		return codons;
	}


	//	private String chromosome;
	//	private int start;
	//	private int end;
	//	private String id;
	//	private String name;
	//	private String type;
	//	private String biotype;
	//	private String featureChromosome;
	//	private int featureStart;
	//	private int featureEnd;
	//	private String featureStrand;
	//
	//	private String snpId;
	//	private String ancestral;
	//	private String alternative;
	//
	//	private String geneId;
	//	private String transcriptId;
	//	private String geneName;
	//
	//	public String consequenceType;
	//	private String consequenceTypeObo;
	//	private String consequenceTypeDesc;
	//	private String consequenceTypeType;
	//
	//	private String aminoacidChange;
	//	private String codonChange;
	public boolean isShowFeatures() {
		return showFeatures;
	}


	public void setShowFeatures(boolean showFeatures) {
		this.showFeatures = showFeatures;
	}


	public boolean isShowVariation() {
		return showVariation;
	}


	public void setShowVariation(boolean showVariation) {
		this.showVariation = showVariation;
	}


	public boolean isShowRegulatory() {
		return showRegulatory;
	}


	public void setShowRegulatory(boolean showRegulatory) {
		this.showRegulatory = showRegulatory;
	}


	public boolean isShowDiseases() {
		return showDiseases;
	}


	public void setShowDiseases(boolean showDiseases) {
		this.showDiseases = showDiseases;
	}


}
