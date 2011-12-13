package org.bioinfo.infrared.lib.impl.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bioinfo.infrared.core.cellbase.Exon;
import org.bioinfo.infrared.lib.api.ExonDBAdaptor;
import org.bioinfo.infrared.lib.common.Position;
import org.bioinfo.infrared.lib.common.Region;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;


public class ExonHibernateDBAdaptor extends HibernateDBAdaptor implements ExonDBAdaptor {


	
	public ExonHibernateDBAdaptor(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Exon> getAll() {
		Criteria criteria = this.openSession().createCriteria(Exon.class);
		return (List<Exon>) executeAndClose(criteria);
	}

	@Override
	public List<String> getAllIds() {
		return this.getAllEnsemblIds();
	}
	
	/*********************No se implementan todavia*/
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
	/************************/

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllEnsemblIds() {
		Query query = this.openSession().createQuery("select e.stableId from Exon e");
		return (List<String>) executeAndClose(query);
	}

	/****/
	@Override
	public Exon getByEnsemblId(String ensemblId) {
		Session session = this.openSession();
		Exon exon = this.getByEnsemblId(ensemblId, session);
		session.close();
		return exon;
	}
	@SuppressWarnings("unchecked")
	private Exon getByEnsemblId(String ensemblId, Session session) {
		Criteria criteria = session.createCriteria(Exon.class);
		criteria.add(Restrictions.eq("stableId", ensemblId.trim()));
		List<Exon> exons = (List<Exon>) criteria.list();
		if(exons != null && exons.size() > 0) {
			return exons.get(0);
		}else {
			return null;
		}
	}
	@Override
	public List<Exon> getAllByEnsemblIdList(List<String> ensemblIds) {
		Session session = this.openSession();
		List<Exon> exons = new ArrayList<Exon>(ensemblIds.size());
		for(String ensemblId: ensemblIds) {
			exons.add(this.getByEnsemblId(ensemblId,session));
		}
		session.close();
		return exons;
	}
	/****/

	/****/
	@Override
	public List<Exon> getByEnsemblTranscriptId(String transcriptId) {
		Session session =  this.openSession();
		List<Exon> exons = this.getByEnsemblTranscriptId(transcriptId,session);
		session.close();
		return exons;
	}
	@SuppressWarnings("unchecked")
	private List<Exon> getByEnsemblTranscriptId(String transcriptId, Session session) {
		Criteria criteria =  session
		.createCriteria(Exon.class)
		.createCriteria("exonToTranscripts")
		.createCriteria("transcript").add( Restrictions.eq("stableId", transcriptId.trim()));
		return (List<Exon>)criteria.list();
	}
	@Override
	public List<List<Exon>> getByEnsemblTranscriptIdList(List<String> transcriptIds) {
		Session session =  this.openSession();
		List<List<Exon>> exonsList = new ArrayList<List<Exon>>(transcriptIds.size());
		for(String transcriptId: transcriptIds) {
			exonsList.add(this.getByEnsemblTranscriptId(transcriptId, session));
		}
		session.close();
		return exonsList;
	}
	/****/
	
	/****/
	@Override
	public List<Exon> getByEnsemblGeneId(String geneId) {
		Session session =  this.openSession();
		List<Exon> exons = this.getByEnsemblGeneId(geneId,session);
		session.close();
		return exons;
	}
	@SuppressWarnings("unchecked")
	private List<Exon> getByEnsemblGeneId(String geneId, Session session) {
		Criteria criteria =  session
				.createCriteria(Exon.class).setFetchMode("exon2transcripts", FetchMode.SELECT)
				.createCriteria("exonToTranscripts").setFetchMode("transcript", FetchMode.SELECT)
				.createCriteria("transcript").setFetchMode("gene", FetchMode.SELECT)
				.createCriteria("gene").add( Restrictions.eq("stableId", geneId.trim()));
		return (List<Exon>)criteria.list();
	}
	@Override
	public List<List<Exon>> getByEnsemblGeneIdList(List<String> geneIds) {
		Session session =  this.openSession();
		List<List<Exon>> exonsList = new ArrayList<List<Exon>>(geneIds.size());
		for(String geneId: geneIds) {
			exonsList.add(this.getByEnsemblGeneId(geneId, session));
		}
		session.close();
		return exonsList;
	}
	/****/
	
	
	/***/
	@Override
	public List<Exon> getAllByPosition(String chromosome, int position) {
		Session session =  this.openSession();
		List<Exon> exons = this.getAllByPosition(chromosome,position,session);
		session.close();
		return exons;
	}
	@Override
	public List<Exon> getAllByPosition(Position position) {
		if(position == null) {
			return null;
		}else {
			return getAllByPosition(position.getChromosome(), position.getPosition());	
		}
	}
	@SuppressWarnings("unchecked")
	private List<Exon> getAllByPosition(String chromosome, int position, Session session) {
		Criteria criteria =  session.createCriteria(Exon.class);
		criteria.add(Restrictions.eq("chromosome", chromosome)).add(Restrictions.ge("end", position)).add(Restrictions.le("start", position));
		return (List<Exon>)criteria.list();
	}
	@Override
	public List<List<Exon>> getAllByPositionList(List<Position> positions) {
		Session session =  this.openSession();
		List<List<Exon>> exonsList = new ArrayList<List<Exon>>(positions.size());
		for(Position position: positions) {
			exonsList.add(this.getAllByPosition(position.getChromosome(), position.getPosition(), session));
		}
		session.close();
		return exonsList;
	}
	/****/
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Exon> getAllByRegion(String chromosome) {
		Criteria criteria =  this.openSession().createCriteria(Exon.class);
		criteria.add(Restrictions.eq("chromosome", chromosome));
		return (List<Exon>)executeAndClose(criteria);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Exon> getAllByRegion(String chromosome, int start) {
		Criteria criteria =  this.openSession().createCriteria(Exon.class);
		criteria.add(Restrictions.eq("chromosome", chromosome)).add(Restrictions.ge("end", start));
		return (List<Exon>)executeAndClose(criteria);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Exon> getAllByRegion(String chromosome, int start, int end) {
		Criteria criteria =  this.openSession().createCriteria(Exon.class);
		criteria.add(Restrictions.eq("chromosome", chromosome)).add(Restrictions.ge("end", start)).add(Restrictions.le("start", end));
		return (List<Exon>)executeAndClose(criteria);
	}
	
	@Override
	public List<Exon> getAllByRegion(Region region) {
		if(region == null) {
			return null;
		}else {
			return getAllByRegion(region.getChromosome(), region.getStart(), region.getEnd());	
		}
	}

	@Override
	public List<List<Exon>> getAllByRegionList(List<Region> regions) {
		List<List<Exon>> exonsList = new ArrayList<List<Exon>>(regions.size());
		for(Region region: regions) {
			exonsList.add(getAllByRegion(region));
		}
		return exonsList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Exon> getAllByCytoband(String chromosome, String cytoband) {
		Query query = this.openSession().createQuery("select e from Exon e, Cytoband k where k.chromosome= :chromosome and k.cytoband = :cytoband and k.chromosome=e.chromosome and e.end>=k.start and e.start<=k.end").setParameter("chromosome", chromosome).setParameter("cytoband", cytoband);
		return (List<Exon>)executeAndClose(query);
	}
	
	/****/
	@Override
	public List<Exon> getAllBySnpId(String snpNameId) {
		Session session = this.openSession();
		List<Exon> list =  this.getAllBySnpId(snpNameId, session);
		session.close();
		return list;
	}
	@SuppressWarnings("unchecked")
	public List<Exon> getAllBySnpId(String snpNameId, Session session) {
		Criteria criteria = session
				.createCriteria(Exon.class)
				.createCriteria("exonToTranscripts")
				.createCriteria("transcript")
				.createCriteria("snpToTranscripts")
				.createCriteria("snp").add(Restrictions.eq("name", snpNameId));
		return (List<Exon>)criteria.list();
	}
	@Override
	public List<List<Exon>> getAllBySnpIdList(List<String> snpNameIds) {
		Session session = this.openSession();
		List<List<Exon>> exons = new ArrayList<List<Exon>>(snpNameIds.size());
		for(String id: snpNameIds) {
			exons.add(getAllBySnpId(id,session));
		}
		session.close();
		return exons;
	}
	/****/
	
	@Override
	public Region getRegionById(String ensemblId) {		
		Exon exon =  this.getByEnsemblId(ensemblId);
		return new Region(exon.getChromosome(),exon.getStart(),exon.getEnd());
	}

	@Override
	public List<Region> getAllRegionsByIdList(List<String> ensemblIdList) {
		List<Region> regions = new ArrayList<Region>(ensemblIdList.size());
		List<Exon> Exons = getAllByEnsemblIdList(ensemblIdList);
		for(Exon exon: Exons) {
			regions.add(new Region(exon.getChromosome(),exon.getStart(),exon.getEnd()));
		}
		return regions;
	}

	@Override
	public String getSequenceById(String ensemblId) {
		Exon exon = this.getByEnsemblId(ensemblId);
		GenomeSequenceDBAdaptor da = new GenomeSequenceDBAdaptor(this.getSessionFactory());
		return da.getByRegion(exon.getChromosome(),exon.getStart(),exon.getEnd()).getSequence();
	}

	@Override
	public List<String> getAllSequencesByIdList(List<String> ensemblIdList) {
		List<String> sequence = new ArrayList<String>(ensemblIdList.size());
		List<Exon> Exons = getAllByEnsemblIdList(ensemblIdList);
		GenomeSequenceDBAdaptor da = new GenomeSequenceDBAdaptor(this.getSessionFactory());
		for(Exon exon: Exons) {
			sequence.add(da.getByRegion(exon.getChromosome(),exon.getStart(),exon.getEnd()).getSequence());
		}
		return sequence;
	}


}