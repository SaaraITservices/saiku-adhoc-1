/*
 * Copyright (C) 2011 Marius Giepz
 *
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation; either version 2 of the License, or (at your option) 
 * any later version.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 *
 */

package org.saiku.adhoc.server.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.util.QueryXmlHelper;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.core.system.objfac.StandaloneObjectFactory;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterDefinition;
import org.pentaho.reporting.engine.classic.extensions.datasources.cda.CdaDataFactory;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultWizardSpecification;
import org.saiku.adhoc.exceptions.ModelException;
import org.saiku.adhoc.model.master.DerivedModelsCollection;
import org.saiku.adhoc.server.datasource.ICDAManager;

import pt.webdetails.cda.connections.Connection;
import pt.webdetails.cda.connections.metadata.MetadataConnection;
import pt.webdetails.cda.dataaccess.DataAccess;
import pt.webdetails.cda.dataaccess.MqlDataAccess;
import pt.webdetails.cda.settings.CdaSettings;

public class DerivedModelsCollectionServer extends DerivedModelsCollection{
	
    private ICDAManager cdaManager;

    
    public DerivedModelsCollectionServer(String sessionId, Domain domain,
            LogicalModel model) {
        super(sessionId, domain, model);
    }

	public DerivedModelsCollectionServer(String sessionId, Domain domain, LogicalModel model, ICDAManager cdaManager2) {
	    super(sessionId, domain, model);
	    this.cdaManager = cdaManager2;
    }

    public void init() throws ModelException{
        
        
        //init all the stuff
        this.query = new Query(domain, logicalModel);      
        this.xmlHelper = new QueryXmlHelper();  
        this.groups = new ArrayList<String>();
        this.groupIdToName = new HashMap<String, String>();     
        this.filterQueries = new HashMap<String,Query>();
        //this.filterValues = new HashMap<String,ArrayList<String>>();  
        this.paramDef = new DefaultParameterDefinition();

        try{
            //init cda
            this.cda = new CdaSettings("cda" + sessionId, null);
            String[] domainInfo = domain.getId().split("/");
            PentahoSystem.setObjectFactory(new StandaloneObjectFactory());
            Connection connection = new MetadataConnection("1", domainInfo[0]+"/"+domainInfo[1], domainInfo[1]);
            DataAccess dataAccess = new MqlDataAccess(sessionId, sessionId, "1", "") ;
            cda.addConnection(connection);
            cda.addDataAccess(dataAccess);

            //init the wizard-spec
            this.wizardSpec = new DefaultWizardSpecification();     
            wizardSpec.setAutoGenerateDetails(false);

            //Init the Data Factory
            

            String solution = "";
            CdaDataFactory f = new CdaDataFactory();        
            String baseUrlField = null;
            f.setBaseUrlField(baseUrlField);
            String name = this.sessionId;
            String queryString = this.sessionId;
            f.setQuery(name, queryString);          
            //TODO Plugin URL detection
            String baseUrl = "http://localhost:8080/saiku-adhoc-webapp/rest/saiku-adhoc";
            
            
            f.setBaseUrl(baseUrl);
            f.setSolution(solution);
            f.setPath(this.cdaManager.getPath());
            String file =  this.sessionId + ".cda";
            f.setFile(file);        
            String username = "admin";
            f.setUsername(username);
            String password = "admin";
            f.setPassword(password);
            this.cdaDataFactory = f;
                        
        }catch(Exception e){
            throw new ModelException("heavy failure");
        }
	}

	
}
