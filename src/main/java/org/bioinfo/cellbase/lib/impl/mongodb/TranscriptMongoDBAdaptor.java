package org.bioinfo.cellbase.lib.impl.mongodb;

import java.util.List;
import java.util.Map;

import org.bioinfo.cellbase.lib.api.TranscriptDBAdaptor;
import org.bioinfo.cellbase.lib.common.Position;
import org.bioinfo.cellbase.lib.common.Region;
import org.bioinfo.cellbase.lib.common.core.Transcript;
import org.bioinfo.cellbase.lib.impl.dbquery.QueryOptions;
import org.bioinfo.cellbase.lib.impl.dbquery.QueryResponse;
import org.bioinfo.cellbase.lib.impl.dbquery.QueryResult;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;

public class TranscriptMongoDBAdaptor extends MongoDBAdaptor implements TranscriptDBAdaptor {

	public TranscriptMongoDBAdaptor(DB db) {
		super(db);
	}

	public TranscriptMongoDBAdaptor(DB db, String species, String version) {
		super(db, species, version);
		mongoDBCollection = db.getCollection("core");
	}


    @Override
    public QueryResponse getAllById(String id, QueryOptions options) {
        //        db.core.aggregate({$match: {"transcripts.id": "ENST00000343281"}}, {$unwind: "$transcripts"}, {$match: {"transcripts.id": "ENST00000343281"}})

        DBObject[] commands = new DBObject[3];
        DBObject match = new BasicDBObject("$match", new BasicDBObject("transcripts.id", id) );
        DBObject unwind = new BasicDBObject("$unwind", "$transcripts" );
        commands[0] = match;
        commands[1] = unwind;
        commands[2] = match;

        QueryResponse q = executeAggregation(id, commands, options);

        System.out.print(commands[0].toString()+",");
        System.out.print(commands[1].toString()+",");
        System.out.print(commands[2].toString());
        System.out.println("");

        System.out.println(">>"+((QueryResult)q.get(id)).getResult());

        return executeAggregation(id, commands, options);
    }
    
    

    @Override
    public QueryResponse getAllByIdList(List<String> idList, QueryOptions options) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public QueryResponse getAllByPosition(String chromosome, int position,
			QueryOptions options) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryResponse getAllByPosition(Position position,
			QueryOptions options) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryResponse getAllByPositionList(List<Position> positionList,
			QueryOptions options) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryResponse getAllByRegion(String chromosome, int start, int end,
			QueryOptions options) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public QueryResponse getAllByRegion(Region region, QueryOptions options) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryResponse getAllByRegionList(List<Region> regions,
			QueryOptions options) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public QueryResponse getAllByEnsemblExonId(String ensemblExonId,
			QueryOptions options) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryResponse getAllByEnsemblExonIdList(
			List<String> ensemblExonIdList, QueryOptions options) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public QueryResponse getAllByTFBSId(String tfbsId, QueryOptions options) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryResponse getAllByTFBSIdList(List<String> tfbsIdList,
			QueryOptions options) {
		// TODO Auto-generated method stub
		return null;
	}
	
//	private List<List<Transcript>> executeQuery(DBObject query) {
//		List<List<Transcript>> result = null;
//
//		BasicDBObject returnFields = new BasicDBObject("transcripts", 1);
//		DBCursor cursor = mongoDBCollection.find(query, returnFields);
//
//		try {
//			if (cursor != null) {
//				result = new ArrayList<List<Transcript>>();
////				Gson jsonObjectMapper = new Gson();
//				Gene gene = null;
//				while (cursor.hasNext()) {
////					gene = (Gene) jsonObjectMapper.fromJson(cursor.next().toString(), Gene.class);
//					result.add(gene.getTranscripts());
////					BasicDBList b = new BasicDBList();
////					b.addAll((BasicDBList)cursor.next().get("transcripts"));
////					trans = (Transcript) jsonObjectMapper.fromJson(cursor.next().get("transcripts").toString(), Transcript.class);
//				}
//			}
//		} finally {
//			cursor.close();
//		}
//
//		return result;
//	}

	
//	@Override
//	public List<List<Transcript>> getAllByName(String name, List<String> exclude) {
//		BasicDBObject query = new BasicDBObject("transcripts.xrefs.id", name.toUpperCase());
//        List<List<Transcript>> result = new ArrayList<List<Transcript>>();
//        List<List<Transcript>> transcriptsList = executeQuery(query);
//
//        boolean found = false;
//		for (List<Transcript> transcripts : transcriptsList) {
//
//            found = false;
//            for (Transcript transcript : transcripts) {
//                for (Xref xref : transcript.getXrefs()) {
//                    if (xref.getId().equals(name.toUpperCase())) {
//                        result.add(transcripts);
//                        found = true;
//                        break;
//                    }
//                }
//                if(found){
//                    break;
//                }
//            }
//
//
//		}
//		return result;
//	}


	@Override
	public List<Transcript> getAllByProteinName(String proteinName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<List<Transcript>> getAllByProteinNameList(List<String> proteinNameList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Transcript> getAllByMirnaMature(String mirnaID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<List<Transcript>> getAllByMirnaMatureList(List<String> mirnaIDList) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public QueryResponse getAll(QueryOptions options) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryResponse next(String id, QueryOptions options) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueryResponse next(String chromosome, int position, QueryOptions options) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public List<String> getAllIds() {
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
	public Region getRegionById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Region> getAllRegionsByIdList(List<String> idList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSequenceById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllSequencesByIdList(List<String> idList) {
		// TODO Auto-generated method stub
		return null;
	}

}
