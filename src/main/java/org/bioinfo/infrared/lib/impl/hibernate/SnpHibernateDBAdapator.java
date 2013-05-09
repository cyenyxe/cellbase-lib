package org.bioinfo.infrared.lib.impl.hibernate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bioinfo.infrared.core.cellbase.ConsequenceType;
import org.bioinfo.infrared.core.cellbase.FeatureMap;
import org.bioinfo.infrared.core.cellbase.Snp;
import org.bioinfo.infrared.core.cellbase.SnpPhenotypeAnnotation;
import org.bioinfo.infrared.core.cellbase.SnpPopulationFrequency;
import org.bioinfo.infrared.core.cellbase.SnpToTranscript;
import org.bioinfo.infrared.core.cellbase.SnpToTranscriptConsequenceType;
import org.bioinfo.infrared.lib.api.SnpDBAdaptor;
import org.bioinfo.infrared.lib.common.IntervalFeatureFrequency;
import org.bioinfo.infrared.lib.common.Position;
import org.bioinfo.infrared.lib.common.Region;
import org.bioinfo.infrared.lib.common.SnpRegulatoryConsequenceType;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

class SnpHibernateDBAdapator extends HibernateDBAdaptor implements SnpDBAdaptor {

	private int MAX_BATCH_QUERIES_LIST = 50;
	//	private int FEATURE_MAP_CHUNK_SIZE = 400;


	public SnpHibernateDBAdapator(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public SnpHibernateDBAdapator(SessionFactory sessionFactory, String species, String version) {
		super(sessionFactory, species, version);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Snp> getAll() {
		Criteria criteria = this.openSession().createCriteria(Snp.class).setMaxResults(500000);
		return (List<Snp>) this.executeAndClose(criteria);
	}

	//	@Override
	//	public List<Snp> getAllByDbSnpId(String id){
	//		Session session = this.openSession();
	//		List<Snp> snps = this.getByDbSnpId(id, session);
	//		session.close();
	//		return snps;
	//	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Snp> getAllBySnpId(String name) {
		Criteria criteria = this.openSession().createCriteria(Snp.class)
				.add(Restrictions.eq("name", name));
		return (List<Snp>) executeAndClose(criteria);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<List<Snp>> getAllBySnpIdList(List<String> nameList){
		List<List<Snp>> result = new ArrayList<List<Snp>>(nameList.size());
		Criteria criteria = null;
		Session session = this.openSession();
		for(String name: nameList) {
			criteria = session.createCriteria(Snp.class)
					.add(Restrictions.eq("name", name));
			result.add((List<Snp>)execute(criteria));
		}
		session.close();
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Snp> getAllByGeneName(String geneId) {
		Criteria criteria = this.openSession().createCriteria(Snp.class)
				.createCriteria("snpToTranscripts")
				.createCriteria("transcript")
				.createCriteria("transcriptToXrefs")
				.createCriteria("xref")
				.add(Restrictions.eq("displayId", geneId));

		//	GeneHibernateDBAdaptor geneHibernateDBAdaptor = new GeneHibernateDBAdaptor(this.getSessionFactory());
		//	Gene gene = geneHibernateDBAdaptor.getByEnsemblId(ensemblId);
		//	return this.getAllByRegion(gene.getChromosome(), gene.getStart(), gene.getEnd());
		return (List<Snp>) executeAndClose(criteria);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<List<Snp>> getAllByGeneNameList(List<String> geneIds) {
		List<List<Snp>> result = new ArrayList<List<Snp>>(geneIds.size());
		Criteria criteria = null;
		Session session = this.openSession();
		for(String id: geneIds) {
			criteria = session.createCriteria(Snp.class)
					.createCriteria("snpToTranscripts")
					.createCriteria("transcript")
					.createCriteria("transcriptToXrefs")
					.createCriteria("xref")
					.add(Restrictions.eq("displayId", id));
			result.add((List<Snp>) execute(criteria));
		}
		session.close();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Snp> getAllByEnsemblGeneId(String ensemblGeneId) {
		Criteria criteria = this.openSession().createCriteria(Snp.class)
				.createCriteria("snpToTranscripts")
				.createCriteria("transcript")
				.createCriteria("gene")
				.add(Restrictions.eq("stableId", ensemblGeneId));
		return (List<Snp>) executeAndClose(criteria);
	}

	@Override
	public List<List<Snp>> getAllByEnsemblGeneIdList(List<String> ensemblGeneList) {
		List<List<Snp>> result = new ArrayList<List<Snp>>(ensemblGeneList.size());
		for(String id: ensemblGeneList) {
			result.add(this.getAllByEnsemblGeneId(id));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Snp> getAllByEnsemblTranscriptId(String ensemblTranscriptId) {
		Criteria criteria = this.openSession().createCriteria(Snp.class)
				.createCriteria("snpToTranscripts")
				.createCriteria("transcript")
				.add(Restrictions.eq("stableId", ensemblTranscriptId));
		return (List<Snp>) executeAndClose(criteria);
	}

	@Override
	public List<List<Snp>> getAllByEnsemblTranscriptIdList(List<String> ensemblTranscriptList) {
		List<List<Snp>> result = new ArrayList<List<Snp>>(ensemblTranscriptList.size());
		for(String id: ensemblTranscriptList) {
			result.add(this.getAllByEnsemblTranscriptId(id));
		}
		return result;
	}	




	@SuppressWarnings("unchecked")
	@Override
	public List<ConsequenceType> getAllConsequenceTypes() {
		Criteria criteria = this.openSession().createCriteria(ConsequenceType.class);
		return (List<ConsequenceType>) executeAndClose(criteria);
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<ConsequenceType> getAllConsequenceTypesBySnpId(String snpId) {
		ConsequenceType intergenic = new ConsequenceType(17, "SO:0001628", "intergenic_variant", "", "INTERGENIC", 100, "", "Intergenic", "More than 5 kb either upstream or downstream of a transcript");
		Criteria criteria = this.openSession().createCriteria(ConsequenceType.class)
				.createCriteria("snpToTranscripts")
				.createCriteria("snp")
				.add(Restrictions.eq("name", snpId));
		
		List<ConsequenceType> consquenceAux = (List<ConsequenceType>) executeAndClose(criteria);
		if(consquenceAux != null && consquenceAux.size() > 0) {
			return consquenceAux;
		}else {
			return Arrays.asList(intergenic);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<List<SnpToTranscriptConsequenceType>> getAllConsequenceTypesBySnpIdList(List<String> snpIdList) {
		List<List<SnpToTranscriptConsequenceType>> consequenceTypeList = new ArrayList<List<SnpToTranscriptConsequenceType>>(snpIdList.size());
		List<SnpToTranscriptConsequenceType> snpToTransConsquenceTypeListAux;
		
		ConsequenceType intergenic = new ConsequenceType(17, "SO:0001628", "intergenic_variant", "", "INTERGENIC", 100, "", "Intergenic", "More than 5 kb either upstream or downstream of a transcript");
		
		Criteria criteria;
		Session session = this.openSession();
		for(String snpId: snpIdList) {
			criteria = session.createCriteria(SnpToTranscriptConsequenceType.class).setFetchMode("consequenceType", FetchMode.JOIN)
				.createCriteria("snpToTranscript").setFetchMode("transcript", FetchMode.JOIN)
				.createCriteria("snp")
				.add(Restrictions.eq("name", snpId));
			snpToTransConsquenceTypeListAux = (List<SnpToTranscriptConsequenceType>) execute(criteria);
			if(snpToTransConsquenceTypeListAux != null && snpToTransConsquenceTypeListAux.size() > 0) {
				consequenceTypeList.add(snpToTransConsquenceTypeListAux);
			}else {	// SNP is intergenic
				criteria = session.createCriteria(Snp.class)
					.add(Restrictions.eq("name", snpId));
				List<Snp> snp = (List<Snp>) execute(criteria);
				SnpToTranscript st = new SnpToTranscript();
				if(snp != null && snp.size() > 0) {
					st.setSnp(snp.get(0));
				}
				SnpToTranscriptConsequenceType inter = new SnpToTranscriptConsequenceType(0, st, intergenic);
				consequenceTypeList.add(Arrays.asList(inter));
			}
		}
		session.close();
		return consequenceTypeList;
	}

	@Override
	public List<SnpToTranscript> getAllSnpToTranscriptsBySnpId(String snpId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SnpToTranscript> getAllSnpToTranscriptsByTranscriptId(String transcriptId) {
		// TODO Auto-generated method stub
		return null;
	}

	//XXX
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllIdsBySOConsequenceType(String consequenceType) {
		Query query = this.openSession().createQuery("select s.name from Snp s where s.displaySoConsequence= :CONSQ_TYPE").setParameter("CONSQ_TYPE", consequenceType).setMaxResults(500000);
		return (List<String>) executeAndClose(query);
	}

	//XXX
	@Override
	public List<List<String>> getAllIdsBySOConsequenceTypeList(List<String> consequenceTypeList) {
		List<List<String>> results = new ArrayList<List<String>>(consequenceTypeList.size());
		for (String consequenceType : consequenceTypeList) {
			List<String> idsList = this.getAllIdsBySOConsequenceType(consequenceType);
			results.add(idsList);
		}
		return results;
	}

	//XXX
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllIdsByRegion(String chromosome, int start, int end) {

		//		int chunk_size = applicationProperties.getIntProperty("CHUNK_SIZE", 400);
		//		int start_chunk = start / chunk_size;
		//		int end_chunk = end / chunk_size;
		//		Query query = this.openSession().createQuery("select distinct fm.featureName from FeatureMap fm where fm.featureType='snp' and fm.chromosome= :CHROMOSOME and fm.chunkId >= :START and fm.chunkId <= :END")
		//										.setParameter("CHROMOSOME", chromosome)
		//										.setParameter("START", start_chunk)
		//										.setParameter("END", end_chunk);
		/*
		 * Accessing to snp table gives an 6x of speed up when compared to FeatureMap
		 */
		Query query = this.openSession().createQuery("select distinct(s.name) from Snp s where s.chromosome= :CHROMOSOME and s.start >= :START and s.end <= :END")
				.setParameter("CHROMOSOME", chromosome)
				.setParameter("START", start)
				.setParameter("END", end);

		return (List<String>) executeAndClose(query);
	}




	@SuppressWarnings("unchecked")
	@Override
	public List<Snp> getAllBySOConsequenceType(String consequenceType) {
		Criteria criteria = this.openSession().createCriteria(Snp.class)
				.add(Restrictions.eq("displaySoConsequence", consequenceType.trim())).setMaxResults(200000);
		return (List<Snp>)executeAndClose(criteria);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Snp> getAllBySOConsequenceTypeList(List<String> consequenceTypeList) {
		Criteria criteria = this.openSession().createCriteria(Snp.class).setCacheable(true).setFetchMode("SnpToTranscript", FetchMode.JOIN);
		for(String consequenceType : consequenceTypeList) {
			criteria.add(Restrictions.disjunction().add(Restrictions.eq("displaySoConsequence", consequenceType)));
		}
		return (List<Snp>)executeAndClose(criteria);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Snp> getAllByEnsemblConsequenceType(String ensemblConsequenceType) {
		Criteria criteria = this.openSession().createCriteria(Snp.class)
				.add(Restrictions.eq("displayConsequence", ensemblConsequenceType.trim()));
		return (List<Snp>)executeAndClose(criteria);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Snp> getAllByEnsemblConsequenceTypeList(List<String> ensemblConsequenceTypeList) {
		Criteria criteria = this.openSession().createCriteria(Snp.class).setCacheable(true).setFetchMode("SnpToTranscript", FetchMode.JOIN);
		for(String consequenceType : ensemblConsequenceTypeList) {
			criteria.add(Restrictions.disjunction().add(Restrictions.eq("displayConsequence", consequenceType)));
		}
		return (List<Snp>)executeAndClose(criteria);
	}




	@Override
	public List<Snp> getAllByPosition(String chromosome, int position) {
		return this.getAllByRegion(new Region(chromosome, position, position));
	}

	@Override
	public List<Snp> getAllByPosition(Position position) {
		if(position != null){
			return this.getAllByRegion(position.getChromosome(), position.getPosition(), position.getPosition());
		}else{
			return null;	
		}
	}

	@Override
	public List<List<Snp>> getAllByPositionList(List<Position> positionList) {
		List<List<Snp>> results = null;
		if(positionList != null) {
			results = new ArrayList<List<Snp>>(positionList.size());
			for(Position position : positionList) {
				results.add(this.getAllByPosition(position));
			}			
		}
		return results;
	}




	@SuppressWarnings("unchecked")
	@Override
	public List<Snp> getAllByRegion(String chromosome) {
		Criteria criteria = this.openSession().createCriteria(Snp.class)
				.add(Restrictions.eq("chromosome", chromosome));
		return (List<Snp>) this.executeAndClose(criteria);
	}


	@Override
	public List<Snp> getAllByRegion(String chromosome, int start) {
		return this.getAllByRegion(chromosome, start, Integer.MAX_VALUE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Snp> getAllByRegion(String chromosome, int start, int end) {
//		GenomicRegionFeatureDBAdaptor genomicRegionFeatureDBAdaptor = new GenomicRegionFeatureHibernateDBAdaptor(this.getSessionFactory());
//		GenomicRegionFeatures genomicRegionFeatures = genomicRegionFeatureDBAdaptor.getByRegion(new Region(chromosome, start, end), Arrays.asList("snp"));
//		return genomicRegionFeatures.getSnp();
		Criteria criteria =  this.openSession().createCriteria(Snp.class);
		criteria.add(Restrictions.eq("chromosome", chromosome))
			.add(Restrictions.ge("end", start))
			.add(Restrictions.le("start", end))
			.addOrder(Order.asc("start"));
		return (List<Snp>) executeAndClose(criteria);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Snp> getAllByRegion(String chromosome, int start, int end, List<String> consequenceTypeList) {
		Criteria criteria =  this.openSession().createCriteria(Snp.class);
		criteria.add(Restrictions.eq("chromosome", chromosome))
			.add(Restrictions.ge("end", start))
			.add(Restrictions.le("start", end))
			.add(Restrictions.in("displaySoConsequence", consequenceTypeList))
			.addOrder(Order.asc("start"));
		return (List<Snp>) executeAndClose(criteria);
	}

	@Override
	public List<Snp> getAllByRegion(Region region) {
		return this.getAllByRegion(region.getChromosome(), region.getStart(), region.getEnd());
	}

	@Override
	public List<Snp> getAllByRegion(Region region, List<String> consequenceTypeList) {
		return this.getAllByRegion(region.getChromosome(), region.getStart(), region.getEnd(), consequenceTypeList);
	}

	@Override
	public List<List<Snp>> getAllByRegionList(List<Region> regionList) {
		List<List<Snp>> results = new ArrayList<List<Snp>>();
		for (Region region : regionList) {
			results.add(this.getAllByRegion(region));
		}
		return results;
	}

	@Override
	public List<List<Snp>> getAllByRegionList(List<Region> regionList, List<String> consequenceTypeList) {
		List<List<Snp>> results = new ArrayList<List<Snp>>();
		for (Region region : regionList) {
			results.add(this.getAllByRegion(region, consequenceTypeList));
		}
		return results;
	}

	@Override
	public List<Snp> getAllByCytoband(String chromosome, String cytoband) {
		// TODO Auto-generated method stub
		return null;
	}



	//XXX
	@SuppressWarnings("unchecked")
	@Override
	public List<Snp> getAllFilteredByConsequenceType(List<String> snpIds, String consequence) {
		Criteria criteria =  this.openSession().createCriteria(Snp.class);
		criteria.add(Restrictions.in("name", snpIds))
		.add(Restrictions.eq("displaySoConsequence", consequence))
		.addOrder(Order.asc("name"));

		return (List<Snp>) executeAndClose(criteria);
	}

	//XXX
	@SuppressWarnings("unchecked")
	@Override
	public List<Snp> getAllFilteredByConsequenceType(List<String> snpIds, List<String> consequenceTypes) {
		Criteria criteria =  this.openSession().createCriteria(Snp.class);
		criteria.add(Restrictions.in("name", snpIds))
		.add(Restrictions.in("displaySoConsequence", consequenceTypes))
		.addOrder(Order.asc("name"));

		return (List<Snp>) executeAndClose(criteria);
	}

	@Override
	public void writeAllFilteredByConsequenceType(String consequence,String outfile) {
		// TODO Auto-generated method stub

	}



	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllIds() {
		Query query = this.openSession().createQuery("select s.name from Snp s").setMaxResults(200000);
		return (List<String>) executeAndClose(query);
	}

	@Override
	public List<Region> getAllRegionsByIdList(List<String> idList) {
		List<Region> results = new ArrayList<Region>();
		for (String id : idList) {
			results.add(this.getRegionById(id));
		}
		return results;
	}

	@Override
	public List<String> getAllSequencesByIdList(List<String> idList) {
		List<String> results = new ArrayList<String>();
		for (String id : idList) {
			results.add(this.getSequenceById(id));
		}
		return results;
	}


	@Override
	public Map<String, Object> getFullInfo(String id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Map<String, Object>> getFullInfoByIdList(List<String> idList) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Map<String, Object> getInfo(String id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Map<String, Object>> getInfoByIdList(List<String> idList) {
		// TODO Auto-generated method stub
		return null;
	}

	//XXX
	@Override
	public Region getRegionById(String id) {
		List<Snp> snp =  this.getAllBySnpId(id);
		return new Region(snp.get(0).getChromosome(), snp.get(0).getStart(), snp.get(0).getEnd());
	}

	//XXX
	@Override
	public String getSequenceById(String id) {
		Query query = this.openSession().createQuery("select sequence from Snp snp where snp.name= :SNPID")
			.setParameter("SNPID", id);
		return executeAndClose(query).toString();
	}

	@SuppressWarnings("unchecked")
	public List<IntervalFeatureFrequency> getAllIntervalFrequencies(Region region, int interval) {
		SQLQuery sqlquery = (SQLQuery)this.openSession().createSQLQuery("select (cr.start - "+region.getStart()+") DIV "+interval+" as inter, count(*) from snp cr where cr.chromosome= '"+region.getChromosome()+"' and cr.start <= "+region.getEnd()+" and cr.end >= "+region.getStart()+" group by inter").setTimeout(60);
		List<Object[]> objectList =  (List<Object[]>) executeAndClose(sqlquery);

		int CHUNK_SIZE = 50;
		if(interval > 10000) {
			CHUNK_SIZE = 500;
		}
		interval = (interval / CHUNK_SIZE) * CHUNK_SIZE;
		interval = Math.max(interval, 500);

		int numSnps = -1;
		long t1 = System.currentTimeMillis();
		
		String value = getDatabaseQueryCache(species.toUpperCase()+".NUM.SNP.CHR."+region.getChromosome().toUpperCase());
		if(value == null || value.equals("")) {
			sqlquery = this.openSession().createSQLQuery("select count(*) from snp where chromosome= '"+region.getChromosome()+"' ");
			List<BigInteger> integerList =  (List<BigInteger>) executeAndClose(sqlquery);
			putDatabaseQueryCache(species.toUpperCase()+".NUM.SNP.CHR."+region.getChromosome().toUpperCase(), ""+integerList.get(0).intValue());
			value = ""+integerList.get(0).intValue();
		}
		numSnps = Integer.parseInt(value);
		System.out.println("num snps db cached: "+numSnps+", species: "+species+", time: "+(System.currentTimeMillis()-t1));

		t1 = System.currentTimeMillis();
		double maxSnpsInterval = 1;
		value = getDatabaseQueryCache(species.toUpperCase()+".NUM.SNP.CHR."+region.getChromosome().toUpperCase()+".INTERVAL."+interval);
		if(value == null || value.equals("")) {
			sqlquery = (SQLQuery)this.openSession().createSQLQuery("select (cr.start - 1) DIV "+interval+" as inter, LOG(count(*)) as t from snp cr where cr.chromosome= '"+region.getChromosome()+"' and cr.start <= "+Integer.MAX_VALUE+" and cr.end >= 1 group by inter order by t DESC limit 1 ").setTimeout(60);
			List<Object[]> integerList =  (List<Object[]>) executeAndClose(sqlquery);
			if(integerList != null && integerList.size() > 0) {
				System.out.println(">>Cached: "+integerList.get(0)[1]+", interval: "+interval);
				maxSnpsInterval = ((Double)integerList.get(0)[1]).doubleValue();
			}
			putDatabaseQueryCache(species.toUpperCase()+".NUM.SNP.CHR."+region.getChromosome().toUpperCase()+".INTERVAL."+interval, ""+maxSnpsInterval);
			value = ""+maxSnpsInterval;
		}
		maxSnpsInterval = Double.parseDouble(value);
		System.out.println("max num snps db cached per interval: "+maxSnpsInterval+", species: "+species+", time: "+(System.currentTimeMillis()-t1));
		
//		if(!cachedQuerySizes.containsKey(species.toUpperCase()+".NUM.SNP.CHR."+region.getChromosome().toUpperCase())) {
//			sqlquery = this.openSession().createSQLQuery("select count(*) from snp where chromosome= '"+region.getChromosome()+"' ");
//			List<BigInteger> integerList =  (List<BigInteger>) executeAndClose(sqlquery);
//			cachedQuerySizes.put(species.toUpperCase()+".NUM.SNP.CHR."+region.getChromosome().toUpperCase(), integerList.get(0).intValue());
//		}
//		numSnps = (Integer) cachedQuerySizes.get(species.toUpperCase()+".NUM.SNP.CHR."+region.getChromosome().toUpperCase());
//		System.out.println("num snps: "+numSnps+", time: "+(System.currentTimeMillis()-t1));

//		t1 = System.currentTimeMillis();
//		double maxSnpsInterval = 1;
//		if(!cachedQuerySizes.containsKey(species.toUpperCase()+".NUM.SNP.CHR."+region.getChromosome().toUpperCase()+".INTERVAL."+interval)) {
//			sqlquery = this.openSession().createSQLQuery("select (cr.start - 1) DIV "+interval+" as inter, LOG(count(*)) as t from snp cr where cr.chromosome= '"+region.getChromosome()+"' and cr.start <= "+Integer.MAX_VALUE+" and cr.end >= 1 group by inter order by t DESC limit 1 ");
//			List<Object[]> integerList =  (List<Object[]>) executeAndClose(sqlquery);
//			if(integerList != null && integerList.size() > 0) {
//				System.out.println(">>Cached: "+integerList.get(0)[1]+", interval: "+interval);
//				maxSnpsInterval = ((Double)integerList.get(0)[1]).doubleValue();				
//			}
//			cachedQuerySizes.put(species.toUpperCase()+".NUM.SNP.CHR."+region.getChromosome().toUpperCase()+".INTERVAL."+interval, maxSnpsInterval);
//		}
//		maxSnpsInterval  =(Double) cachedQuerySizes.get(species.toUpperCase()+".NUM.SNP.CHR."+region.getChromosome().toUpperCase()+".INTERVAL."+interval);
//		System.out.println("max num snps per interval: "+maxSnpsInterval+", time: "+(System.currentTimeMillis()-t1));
		
		List<IntervalFeatureFrequency> intervalFreqsList = getIntervalFeatureFrequencies(region , interval, objectList, numSnps, maxSnpsInterval);//, numSnps, maxSnpsInterval);
		return intervalFreqsList;
	}


	@Deprecated
	private List<List<Snp>> Reorder(List<String> idList){
		String query = "select snp from Snp as snp left join fetch snp.snpToTranscripts as stt  left join fetch snp.snpXrefs as sxr  left join fetch snp.snp2functionals as s2f left join fetch stt.consequenceType as consequenceType where snp.name in :name";
		List<Snp> result = query(query, idList);
		List<List<Snp>> cleanResult = new ArrayList<List<Snp>>();
		if(result.size() != idList.size()) {
			String resultId = new String(); 
			String prevResultId = new String();
			for(int i=0,j=0; i<idList.size();) {
				if (j < result.size()){
					resultId = result.get(j).getName();
					if (resultId.equals(prevResultId)){
						// REPETIDO 
						cleanResult.get(cleanResult.size() -1).add(result.get(j));
						prevResultId = resultId;
						j++;
					}
					else{
						if( idList.get(i).equals(result.get(j).getName())) {
							List<Snp> list = new ArrayList<Snp>();
							list.add(result.get(j));
							cleanResult.add(list);
							prevResultId = resultId;
							i++;
							j++;
						}else{
							cleanResult.add(null);
							i++;
						}
					}
				}
				else{
					cleanResult.add(null);
					i++;
				}
			}	
		}
		System.out.println("cleanResult " + cleanResult.size());
		return cleanResult;
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	private List<Snp> query(String queryHQL, List<String> idList){
		Session session = this.openSession();
		Query query = session.createQuery(queryHQL);

		List<Snp> result = new ArrayList<Snp>();
		if (idList.size() > MAX_BATCH_QUERIES_LIST){
			for (int i = 0; i < (idList.size()/MAX_BATCH_QUERIES_LIST); i++) {
				int start = (i * MAX_BATCH_QUERIES_LIST );
				int end = start + MAX_BATCH_QUERIES_LIST;

				query.setParameterList("name", idList.subList(start, end));
				result.addAll((Collection<? extends Snp>) this.execute(query));

			}

			if ( (idList.size() % MAX_BATCH_QUERIES_LIST) != 0){
				int start = ( idList.size() /MAX_BATCH_QUERIES_LIST) * MAX_BATCH_QUERIES_LIST;
				int end = idList.size();

				query.setParameterList("name", idList.subList(start, end));
				result.addAll((Collection<? extends Snp>) this.execute(query));
			}
		}
		else{
			query.setParameterList("name", idList);
			result.addAll((Collection<? extends Snp>) this.execute(query));
		}
		closeSession();
		return result;
	}

	

	@Override
	public List<SnpRegulatoryConsequenceType> getAllSnpRegulatoryBySnpName(	String name) {
		return getAllSnpRegulatoryBySnpNameList(Arrays.asList(name)).get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<List<SnpRegulatoryConsequenceType>> getAllSnpRegulatoryBySnpNameList(List<String> nameList) {
		List<List<SnpRegulatoryConsequenceType>> result = new ArrayList<List<SnpRegulatoryConsequenceType>>(nameList.size());
		List<FeatureMap> featMapList = new ArrayList<FeatureMap>();
		List<SnpRegulatoryConsequenceType> aux;
		Session session = this.openSession();
		for(String snpName: nameList) {
			featMapList = (List<FeatureMap>) session.createSQLQuery("select fm2.* from feature_map fm1, feature_map fm2 where fm1.feature_type='snp' and fm1.feature_name= :SNP and fm1.chunk_id=fm2.chunk_id and fm1.chromosome=fm2.chromosome and fm1.start<=fm2.end and fm1.end>=fm2.start and fm2.feature_category='regulatory'")
					.addEntity(FeatureMap.class)
					.setParameter("SNP", snpName).list();
			if(featMapList != null && featMapList.size() > 0) {
				aux = new ArrayList<SnpRegulatoryConsequenceType>(featMapList.size());
				for(FeatureMap fm: featMapList) {
					aux.add(new SnpRegulatoryConsequenceType(snpName, fm.getFeatureName(), fm.getFeatureType(), fm.getChromosome(), fm.getStart(), fm.getEnd(), fm.getStrand(), fm.getTranscriptStableId(), fm.getGeneStableId(), fm.getGeneName(), fm.getBiotype()));
				}
				result.add(aux);
			}else {
				result.add(new ArrayList<SnpRegulatoryConsequenceType>(0));
			}
		}
		session.close();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SnpPhenotypeAnnotation> getAllSnpPhenotypeAnnotationBySnpName(String name) {
		Criteria criteria = this.openSession().createCriteria(SnpPhenotypeAnnotation.class)
			.createCriteria("snp")
			.add(Restrictions.eq("name", name));
		return (List<SnpPhenotypeAnnotation>) executeAndClose(criteria);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<List<SnpPhenotypeAnnotation>> getAllSnpPhenotypeAnnotationListBySnpNameList(List<String> nameList) {
		List<List<SnpPhenotypeAnnotation>> result = new ArrayList<List<SnpPhenotypeAnnotation>>(nameList.size());
		Criteria criteria = null;
		Session session = this.openSession();
		for(String name: nameList) {
			criteria = session.createCriteria(SnpPhenotypeAnnotation.class)
					.createCriteria("snp")
					.add(Restrictions.eq("name", name));
			result.add((List<SnpPhenotypeAnnotation>) execute(criteria));
		}
		session.close();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SnpPhenotypeAnnotation> getAllSnpPhenotypeAnnotationByPosition(	Position position) {
		Criteria criteria = this.openSession().createCriteria(SnpPhenotypeAnnotation.class)
			.createCriteria("snp")
			.add(Restrictions.eq("chromosome", position.getChromosome()))
			.add(Restrictions.le("start", position.getPosition()))
			.add(Restrictions.ge("end", position.getPosition()));
		return (List<SnpPhenotypeAnnotation>) executeAndClose(criteria);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<List<SnpPhenotypeAnnotation>> getAllSnpPhenotypeAnnotationListByPositionList(List<Position> positionList) {
		List<List<SnpPhenotypeAnnotation>> result = new ArrayList<List<SnpPhenotypeAnnotation>>(positionList.size());
		Criteria criteria = null;
		Session session =  this.openSession();
		for(Position position: positionList) {
			criteria = session.createCriteria(SnpPhenotypeAnnotation.class)
				.createCriteria("snp")
				.add(Restrictions.eq("chromosome", position.getChromosome()))
				.add(Restrictions.le("start", position.getPosition()))
				.add(Restrictions.ge("end", position.getPosition()));
			result.add((List<SnpPhenotypeAnnotation>) execute(criteria));
		}
		session.close();
		return result;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SnpPopulationFrequency> getAllSnpPopulationFrequency(String name) {
		Criteria criteria = this.openSession().createCriteria(SnpPopulationFrequency.class)
				.createCriteria("snp")
				.add(Restrictions.eq("name", name));
		return (List<SnpPopulationFrequency>) executeAndClose(criteria);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<List<SnpPopulationFrequency>> getAllSnpPopulationFrequencyList(List<String> nameList) {
		List<List<SnpPopulationFrequency>> result = new ArrayList<List<SnpPopulationFrequency>>(nameList.size());
		Criteria criteria = null;
		Session session = this.openSession();
		for(String name: nameList) {
			criteria = session.createCriteria(SnpPopulationFrequency.class)
				.createCriteria("snp")
				.add(Restrictions.eq("name", name));			
			result.add((List<SnpPopulationFrequency>) execute(criteria));
		}
		session.close();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SnpToTranscript> getAllSnpToTranscript(String name) {
		Criteria criteria = this.openSession().createCriteria(SnpToTranscript.class)
			.setFetchMode("transcript", FetchMode.JOIN)
			.setFetchMode("consequenceType", FetchMode.JOIN)
			.createCriteria("snp")
			.add(Restrictions.eq("name", name));
		return (List<SnpToTranscript>) executeAndClose(criteria);
	}

	@Override
	public List<List<SnpToTranscript>> getAllSnpToTranscriptList(List<String> nameList) {
		List<List<SnpToTranscript>> result = new ArrayList<List<SnpToTranscript>>(nameList.size());
		for(String name: nameList) {
			result.add(this.getAllSnpToTranscript(name));
		}
		return result;
	}

	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ConsequenceType> getAllConsequenceType(String name) {
		Criteria criteria = this.openSession().createCriteria(ConsequenceType.class)
				.createCriteria("snpToTranscripts")
				.createCriteria("snp")
				.add(Restrictions.eq("name", name));
		return (List<ConsequenceType>) executeAndClose(criteria);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<List<ConsequenceType>> getAllConsequenceTypeList(List<String> nameList) {
		List<List<ConsequenceType>> result = new ArrayList<List<ConsequenceType>>(nameList.size());
		Criteria criteria = null;
		Session session = this.openSession();
		for(String name: nameList) {
			criteria = session.createCriteria(ConsequenceType.class)
					.createCriteria("snpToTranscripts")
					.createCriteria("snp")
					.add(Restrictions.eq("name", name));
			result.add((List<ConsequenceType>) execute(criteria));
		}
		session.close();
		return result;
	}


}
