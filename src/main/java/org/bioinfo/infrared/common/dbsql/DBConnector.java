package org.bioinfo.infrared.common.dbsql;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.bioinfo.commons.utils.StringUtils;
import org.bioinfo.db.DBConnection;
import org.bioinfo.db.api.Query;
import org.bioinfo.db.handler.BeanArrayListHandler;


public class DBConnector {

	private DBConnection dbConnection;
	private Properties props = new Properties();

	private String host;
	private String port;
	private String database;
	private String user;
	private String password;
	private String specie;

	public DBConnector() {
		// get parameters from the property file
		loadConfig(new File(System.getenv("INFRARED_HOME")+"/conf/db.conf"));
		createDBConnection();
	}
	public DBConnector(String specie) {
		this.setSpecie(specie);
		// get parameters from the property file
		loadConfig(new File(System.getenv("INFRARED_HOME")+"/conf/db.conf"));
		createDBConnection();
	}

	public DBConnector(File propertyFile) {
		// get parameters from the property file
		loadConfig(propertyFile);
		createDBConnection();
	}

	public DBConnector(String specie, File propertyFile) {
		this.setSpecie(specie);
		// get parameters from the property file
		loadConfig(propertyFile);
		createDBConnection();
	}

	public DBConnector(String specie, String host, String port, String user, String passwd) {
		// get parameters from the property file
		loadConfig(new File(System.getenv("INFRARED_HOME")+"/conf/db.conf"));
		// sobreescribo algunos parametros
		this.host = host;
		this.port = port;
		this.specie = specie;
		this.user = user;
		this.password = passwd;
		createDBConnection();
	}

	public DBConnector(String specie, String host, String port, String user, String passwd, File propertyFile) {
		// get parameters from the property file
		loadConfig(propertyFile);
		// sobreescribo algunos parametros
		this.host = host;
		this.port = port;
		this.specie = specie;
		this.user = user;
		this.password = passwd;
		createDBConnection();
	}

	private void loadConfig(File propertyFile) {
		try {
			props.load(new FileInputStream(propertyFile));
			if(specie == null) {
				this.specie = props.getProperty("DEFAULT.SPECIES");
			}
			if(isValidSpecies(specie)) {
				this.host = props.getProperty("INFRARED.HOSTNAME");
				this.port = props.getProperty("INFRARED.PORT");
				this.database = props.getProperty("INFRARED."+specie.toUpperCase()+".DATABASE");
				this.user = props.getProperty("INFRARED.USER");
				this.password = props.getProperty("INFRARED.PASSWORD");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void createDBConnection() {
		if(getDbConnection() == null) {
			dbConnection = new DBConnection("mysql",host, port, database, user, password);
		}
	}

	public void setAutoConnectAndDisconnect(boolean auto) {
		getDbConnection().setAutoConnectAndDisconnect(auto);
	}

	public void disconnect() throws SQLException {
		getDbConnection().disconnect();
	}

	@SuppressWarnings("unchecked")
	public List<String> getAllChromosomes() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
		Query query = dbConnection.createSQLQuery();
		return (ArrayList<String>)query.execute("select chromosome from karyotype group by chromosome", new BeanArrayListHandler(String.class));
	}

	public List<String> getAvailableDBs() {
		if(props != null && props.get("INFRARED."+specie.toUpperCase()+".AVAILABLE.DBS") != null) {
			return StringUtils.toList((String) props.get("INFRARED."+specie.toUpperCase()+".AVAILABLE.DBS"));
		}else {
			return Collections.emptyList();
		}
	}

	public boolean isValidSpecies(String species) {
		if(props != null && props.get("INFRARED.SPECIES") != null) {
			return StringUtils.toList((String) props.get("INFRARED.SPECIES")).contains(species);
		}else {
			return false;
		}
	}

	/**
	 * @param dbConnection the dbConnection to set
	 */
	public void setDbConnection(DBConnection dbConnection) {
		this.dbConnection = dbConnection;
	}
	/**
	 * @return the dbConnection
	 */
	public DBConnection getDbConnection() {
		return dbConnection;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(host).append("\t").append(port).append("\t").append(database).append("\t").append(user).append("\t").append(password).append("\n");
		return sb.toString();
	}
	/**
	 * @param specie the specie to set
	 */
	public void setSpecie(String specie) {
		this.specie = specie;
	}
	/**
	 * @return the specie
	 */
	public String getSpecie() {
		return specie;
	}
}
