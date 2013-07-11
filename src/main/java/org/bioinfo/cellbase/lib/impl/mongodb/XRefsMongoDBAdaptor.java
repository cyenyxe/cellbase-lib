package org.bioinfo.cellbase.lib.impl.mongodb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bioinfo.cellbase.lib.api.XRefsDBAdaptor;
import org.bioinfo.cellbase.lib.common.XRefs;
import org.bioinfo.cellbase.lib.common.core.DBName;
import org.bioinfo.cellbase.lib.common.core.Gene;
import org.bioinfo.cellbase.lib.common.core.Transcript;
import org.bioinfo.cellbase.lib.common.core.Xref;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

public class XRefsMongoDBAdaptor extends MongoDBAdaptor implements XRefsDBAdaptor {

	public XRefsMongoDBAdaptor(DB db) {
		super(db);
	}

	public XRefsMongoDBAdaptor(DB db, String species, String version) {
		super(db, species, version);
		mongoDBCollection = db.getCollection("core");
	}

//	private List<Xref> executeQuery(DBObject query) {
//		List<Xref> result = null;
//		Set<Xref> xrefSet = new LinkedHashSet<Xref>();
//		
//		BasicDBObject returnFields = new BasicDBObject("transcripts", 1);
//		DBCursor cursor = mongoDBCollection.find(query, returnFields);
//
//		try {
//			if (cursor != null) {
////				Gson jsonObjectMapper = new Gson();
//				Gene gene;
//				while (cursor.hasNext()) {
////					gene = (Gene) jsonObjectMapper.fromJson(cursor.next().toString(), Gene.class);
//					gene = (Gene) jsonObjectMapper.writeValueAsBytes(cursor.next().toString(), Gene.class);
//					for (Transcript transcript : gene.getTranscripts()) {
//						xrefSet.addAll(transcript.getXrefs());
//					}
//				}
//			}
//			result = new ArrayList<Xref>(xrefSet);
//		} finally {
//			cursor.close();
//		}
//
//		return result;
//	}

	@Override
	public List<DBName> getAllDBNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DBName> getAllDBNamesById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DBName> getAllDBNamesByType(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllIdsByDBName(String dbname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Xref> getById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<List<Xref>> getAllByIdList(List<String> idList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Xref> getByStartsWithQuery(String likeQuery) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<List<Xref>> getByStartsWithQueryList(List<String> likeQuery) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Xref> getByStartsWithSnpQuery(String likeQuery) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<List<Xref>> getByStartsWithSnpQueryList(List<String> likeQuery) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Xref> getByContainsQuery(String likeQuery) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<List<Xref>> getByContainsQueryList(List<String> likeQuery) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XRefs getById(String id, String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<XRefs> getAllByIdList(List<String> ids, String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Xref> getByDBName(String id, String dbname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<List<Xref>> getAllByDBName(List<String> ids, String dbname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Xref> getByDBNameList(String id, List<String> dbnames) {

		QueryBuilder builder = QueryBuilder.start("transcripts.xrefs.id").is(id.toUpperCase());
		List<Xref> xrefQuery = new ArrayList<>();//;executeQuery(builder.get());
		logger.info("->>>>>>>>>>>>>>>>"+xrefQuery.size());
		if(dbnames == null) {
			dbnames = Collections.emptyList();
		}
		Set<String> dbnameSet = new HashSet<String>(dbnames);
		
		List<Xref> xrefReturnList = new ArrayList<Xref>(xrefQuery.size());
		for(Xref xref: xrefQuery) {
			if(dbnameSet.size() == 0 || dbnameSet.contains(xref.getDbNameShort())) {
				logger.info("->>>>>>>>>>>>>>>>"+xref.getId());
				xrefReturnList.add(xref);
			}
		}
		return xrefReturnList;
	}

	@Override
	public List<List<Xref>> getAllByDBNameList(List<String> ids, List<String> dbnames) {
		List<List<Xref>> xrefs = new ArrayList<List<Xref>>(ids.size());
		for (String id : ids) {
			xrefs.add(getByDBNameList(id, dbnames));
		}
		return xrefs;
	}

	@Override
	public XRefs getByDBName(String id, String dbname, String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<XRefs> getAllByDBName(List<String> ids, String dbname, String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XRefs getByDBNameList(String id, List<String> dbnames, String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<XRefs> getAllByDBNameList(List<String> ids, List<String> dbnames, String type) {
		// TODO Auto-generated method stub
		return null;
	}

}
