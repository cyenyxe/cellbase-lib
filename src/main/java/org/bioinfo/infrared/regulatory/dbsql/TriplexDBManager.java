package org.bioinfo.infrared.regulatory.dbsql;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.bioinfo.db.handler.BeanArrayListHandler;
import org.bioinfo.infrared.common.dbsql.DBConnector;
import org.bioinfo.infrared.common.dbsql.DBManager;
import org.bioinfo.infrared.common.feature.FeatureList;
import org.bioinfo.infrared.regulatory.Triplex;

public class TriplexDBManager extends DBManager {
	
	private static final String SELECT_FIELDS = " t.triplex_id, g.stable_id, t.relative_start, t.relative_end, t.chromosome, t.absolute_start, t.absolute_end, t.strand, t.length, t.sequence ";
	public static final String GET_BY_SNP_ID = "select "+SELECT_FIELDS+" from snp s, snp2triplex s2t, triplex t, gene g where s.name = ? and s.snp_id=s2t.snp_id and s2t.triplex_id=t.triplex_id and t.gene_id=g.gene_id";
	public static final String GET_ALL_BY_GENE_ID = "select "+SELECT_FIELDS+" from triplex t, gene g where g.stable_id = ? and g.gene_id=t.gene_id ";
	public static final String GET_ALL_BY_POSITION = "select "+SELECT_FIELDS+" from triplex t, gene g where t.gene_id=g.gene_id ";
	
	public TriplexDBManager(DBConnector dBConnector) {
		super(dBConnector);
	}

	@SuppressWarnings("unchecked")
	public FeatureList<Triplex> getAll() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
		return getFeatureList("select "+SELECT_FIELDS+" from triplex t, gene g where t.gene_id=g.gene_id", new BeanArrayListHandler(Triplex.class));
	}

	@SuppressWarnings("unchecked")
	public List<FeatureList<Triplex>> getAllByGeneIds(List<String> geneIds) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
		List<FeatureList<Triplex>> featList = getListOfFeatureListByIds(GET_ALL_BY_GENE_ID, geneIds, new BeanArrayListHandler(Triplex.class));
		return featList;
	}
	
	@SuppressWarnings("unchecked")
	public FeatureList<Triplex> getAllByGeneId(String geneId) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
		return getFeatureListById("select "+SELECT_FIELDS+" from triplex t, gene g where g.stable_id= ? and g.gene_id=t.gene_id ", geneId, new BeanArrayListHandler(Triplex.class));
	}
	
	@SuppressWarnings("unchecked")
	public FeatureList<Triplex> getAllBySnpId(String snpId) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
		return getFeatureListById(GET_BY_SNP_ID, snpId, new BeanArrayListHandler(Triplex.class));
	}
	
	@SuppressWarnings("unchecked")
	public List<FeatureList<Triplex>> getAllBySnpIds(List<String> snpIds) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
		return getListOfFeatureListByIds(GET_BY_SNP_ID, snpIds, new BeanArrayListHandler(Triplex.class));
	}
	
	@SuppressWarnings("unchecked")
	public FeatureList<Triplex> getAllByPosition(String chromosome, int position) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
		return getFeatureList(GET_ALL_BY_POSITION+" and t.chromosome = '"+chromosome+"' and t.absolute_start <= "+position +" and t.absolute_end >= " + position, new BeanArrayListHandler(Triplex.class));
	}

	@SuppressWarnings("unchecked")
	public FeatureList<Triplex> getAllByRegion(String chromosome, int start, int end) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
		return getFeatureList(GET_ALL_BY_POSITION+" and t.chromosome = '"+chromosome+"' and t.absolute_start <= "+ end +" and t.absolute_end >= " + start , new BeanArrayListHandler(Triplex.class));
	}
	
	public void writeAllWithSnps(String outfile) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException {
		writeFeatureList("select s.name as 'SNP name', concat(s.chromosome,':',s.start,'(',s.strand,')') as 'SNP Location', g.stable_id as 'Gene',  t.relative_start, t.relative_end, t.chromosome, t.absolute_start, t.absolute_end, t.strand, t.length, t.sequence from triplex t, snp2triplex s2t, snp s, gene g where t.triplex_id=s2t.triplex_id and s2t.snp_id=s.snp_id and t.gene_id=g.gene_id", new File(outfile));
	}
}
