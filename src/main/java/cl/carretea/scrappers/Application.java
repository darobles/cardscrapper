package cl.carretea.scrappers;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import cl.carretea.scrappers.banks.BancoChile;
import cl.carretea.scrappers.banks.Bice;
import cl.carretea.scrappers.banks.Santander;
import cl.carretea.scrappers.banks.Tenpo;
import cl.carretea.scrappers.banks.Scotiabank;
import cl.carretea.scrappers.banks.Itau;
import cl.carretea.scrappers.banks.Bci;
import cl.carretea.scrappers.banks.Ripley;
import cl.carretea.scrappers.banks.Security;

public class Application {

    public static void main(String[] args) throws InterruptedException  {
    	String uri = "mongodb://carretea:Carrete1109!@54.235.227.133:27017/";
    	Bice bice = new Bice();
    	Santander santander = new Santander();
    	BancoChile bchile = new BancoChile();
    	Scotiabank scotia = new Scotiabank();
    	Tenpo tenpo = new Tenpo();
    	Itau itau = new Itau();
    	Bci bci = new Bci();
    	Ripley ripley = new Ripley();
    	Security security = new Security();
    	List<Promotion> bice_list = bice.runScrapper();
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("bar");
            MongoCollection<Document> collection = database.getCollection("promotions_agust");
            //FindIterable<Document> documentos = collection.find(eq("institution", "Banco de Chile"));

            // Crear un documento
            List<Document> final_list = new ArrayList();
            for(Promotion promotion: bice_list)
            {
                /*for (Document doc : documentos) {
                	if(doc.getString("name_store").equals(promotion.getInstitution_name()))
                	{
                		collection.updateOne(eq("_id", doc.getObjectId("_id")), set("large_description", promotion.getLarge_description()));
                		break;
                	}
                    
                }*/
                Document document = new Document("card_id", promotion.getCard_id())
                		.append("institution", promotion.getInstitution_name())
                        .append("name_store", promotion.getName_store())
                        .append("cards_name", promotion.getCards_name())
                        .append("url_description", promotion.getUrl())
                        .append("discount", promotion.getDiscount())
                        .append("max_discount", promotion.getMax_discount())
                        .append("description", promotion.getDescription())
                        .append("days", promotion.getDays())
                        .append("expiration", promotion.getExpiration_date())
                        .append("large_description", promotion.getLarge_description());
                                               
                final_list.add(document);
                System.out.println(promotion.toString()); 
            	
            }
            if(!final_list.isEmpty())
            	collection.insertMany(final_list);
            System.out.println("Document inserted successfully!");

        }
   }
  
}
