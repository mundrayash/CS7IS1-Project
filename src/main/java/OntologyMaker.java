import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.XSD;

import java.io.FileWriter;


public class OntologyMaker {
    private final static String URI_BASE = "http://www.fake.com/cs7is1-project";
    private final static String NAMESPACE = URI_BASE + "#";
    private final static String ONTOLOGY_PATH = "ontologyFile.ttl";

   public static void createOntology(){
       OntModel model = ModelFactory.createOntologyModel();
       model.setNsPrefix("base", NAMESPACE);

       Ontology ontology = model.createOntology(URI_BASE);
       ontology.addLabel("ireland-weather-station-county", null);

       ontology.addProperty(DCTerms.creator, "Patrick Meleady");
       ontology.addProperty(DCTerms.creator, "Yash Mundra");
       ontology.addProperty(DCTerms.creator, "Caitriona O'Driscoll");
       ontology.addProperty(DCTerms.creator, "Stephen Kelehan");

       String description = "Ireland weather station information, with county information along with rainfall and temperature information." +
               "Weather Stations have several properties including open year, close year and height. They also have " +
               "properties named LocatedIn which provides location information in two seperate formats namely Latitude and" +
               "Longitude and Eastings Northings format.";

       ontology.addProperty(DCTerms.description, description);

       //Weather Station Class
       OntClass weatherStation = model.createClass(NAMESPACE+"WeatherStation");
       weatherStation.addLabel("Weather Station", null);
       weatherStation.addComment("Weather Station contains details about a specific weather station.", null);

       DatatypeProperty stationNumber = model.createDatatypeProperty(NAMESPACE + "stationNumber");
       stationNumber.addLabel("station number", null);
       stationNumber.addComment("ID Number of Weather Station", null);
       stationNumber.setDomain(weatherStation);
       stationNumber.setRange(XSD.integer);
       weatherStation.addSuperClass(model.createCardinalityRestriction(null, stationNumber, 1));

       DatatypeProperty stationName = model.createDatatypeProperty(NAMESPACE + "stationName");
       stationName.addLabel("station name", null);
       stationName.addComment("Name of Weather Station", null);
       stationName.setDomain(weatherStation);
       stationName.setRange(XSD.xstring);
       weatherStation.addSuperClass(model.createCardinalityRestriction(null, stationName, 1));

       DatatypeProperty height = model.createDatatypeProperty(NAMESPACE + "height");
       height.addLabel("height", null);
       height.addComment("Geographical elevation of weather station.", null);
       height.setDomain(weatherStation);
       height.setRange(XSD.integer);
       weatherStation.addSuperClass(model.createCardinalityRestriction(null, height, 1));

       DatatypeProperty openYear = model.createDatatypeProperty(NAMESPACE + "openYear");
       openYear.setLabel("open year",null);
       openYear.setComment("Opening Year of Weather Station", null);
       openYear.setDomain(weatherStation);
       openYear.setRange(XSD.integer);
       weatherStation.addSuperClass(model.createCardinalityRestriction(null, openYear, 1));


       DatatypeProperty closeYear = model.createDatatypeProperty(NAMESPACE + "closeYear");
       closeYear.setLabel("close year",null);
       closeYear.setComment("Closing Year of Weather Station", null);
       closeYear.setDomain(weatherStation);
       closeYear.setRange(XSD.integer);
       weatherStation.addSuperClass(model.createCardinalityRestriction(null, closeYear, 1));


       //Latitude Longitude Location Class
       OntClass latLongLocation = model.createClass(NAMESPACE + "LatLongLocation");
       latLongLocation.addLabel("Latitude Longitude Location", null);
       latLongLocation.addComment("Geographic Location using Latitude and Longitude", null);

       DatatypeProperty latitude = model.createDatatypeProperty(NAMESPACE + "latitude");
       latitude.addLabel("latitude", null);
       latitude.addComment("Geographic latitude", null);
       latitude.setDomain(latLongLocation);
       latitude.setRange(XSD.xfloat);
       latLongLocation.addSuperClass(model.createCardinalityRestriction(null, latitude, 1));


       DatatypeProperty longitude = model.createDatatypeProperty(NAMESPACE + "longitude");
       longitude.addLabel("longitude", null);
       longitude.addComment("Geographic longitude", null);
       longitude.setDomain(latLongLocation);
       longitude.setRange(XSD.xfloat);
       latLongLocation.addSuperClass(model.createCardinalityRestriction(null, longitude, 1));


       //County Class
       OntClass county = model.createClass(NAMESPACE + "County");
       county.addLabel("County", null);
       county.addComment("Irish Counties", null);

       DatatypeProperty area = model.createDatatypeProperty(NAMESPACE + "area");
       area.addLabel("area", null);
       area.addComment("Surface Area of a County", null);
       area.setDomain(county);
       area.setRange(XSD.xfloat);
       county.addSuperClass(model.createCardinalityRestriction(null, area, 1));


       //County Names Class
       OntClass antrim = model.createClass(NAMESPACE + "Antrim");
       antrim.addLabel("Antrim", null);
       antrim.addComment("County in Ireland",null);

       OntClass armagh = model.createClass(NAMESPACE + "Armagh");
       armagh.addLabel("Armagh", null);
       armagh.addComment("County in Ireland",null);

       OntClass carlow = model.createClass(NAMESPACE + "Carlow");
       carlow.addLabel("Carlow", null);
       carlow.addComment("County in Ireland",null);

       OntClass cavan = model.createClass(NAMESPACE + "Cavan");
       cavan.addLabel("Cavan", null);
       cavan.addComment("County in Ireland",null);

       OntClass clare = model.createClass(NAMESPACE + "Clare");
       clare.addLabel("Clare", null);
       clare.addComment("County in Ireland",null);

       OntClass cork = model.createClass(NAMESPACE + "Cork");
       cork.addLabel("Cork", null);
       cork.addComment("County in Ireland",null);

       OntClass derry = model.createClass(NAMESPACE + "Derry");
       derry.addLabel("Derry", null);
       derry.addComment("County in Ireland",null);

       OntClass donegal = model.createClass(NAMESPACE + "Donegal");
       donegal.addLabel("Donegal", null);
       donegal.addComment("County in Ireland",null);

       OntClass down = model.createClass(NAMESPACE + "Down");
       down.addLabel("Down", null);
       down.addComment("County in Ireland",null);

       OntClass dublin = model.createClass(NAMESPACE + "Dublin");
       dublin.addLabel("Dublin", null);
       dublin.addComment("County in Ireland",null);

       OntClass fermanagh = model.createClass(NAMESPACE + "Fermanagh");
       fermanagh.addLabel("Fermanagh", null);
       fermanagh.addComment("County in Ireland",null);

       OntClass galway = model.createClass(NAMESPACE + "Galway");
       galway.addLabel("Galway", null);
       galway.addComment("County in Ireland",null);

       OntClass kerry = model.createClass(NAMESPACE + "Kerry");
       kerry.addLabel("Kerry", null);
       kerry.addComment("County in Ireland",null);

       OntClass kildare = model.createClass(NAMESPACE + "Kildare");
       kildare.addLabel("Kildare", null);
       kildare.addComment("County in Ireland",null);

       OntClass kilkenny = model.createClass(NAMESPACE + "Kilkenny");
       kilkenny.addLabel("Kilkenny", null);
       kilkenny.addComment("County in Ireland",null);

       OntClass laois = model.createClass(NAMESPACE + "Laois");
       laois.addLabel("Laois", null);
       laois.addComment("County in Ireland",null);

       OntClass leitrim = model.createClass(NAMESPACE + "Leitrim");
       leitrim.addLabel("Leitrim", null);
       leitrim.addComment("County in Ireland",null);

       OntClass limerick = model.createClass(NAMESPACE + "Limerick");
       limerick.addLabel("Limerick", null);
       limerick.addComment("County in Ireland",null);

       OntClass longford = model.createClass(NAMESPACE + "Longford");
       longford.addLabel("Longford", null);
       longford.addComment("County in Ireland",null);

       OntClass louth = model.createClass(NAMESPACE + "Louth");
       louth.addLabel("Louth", null);
       louth.addComment("County in Ireland",null);

       OntClass mayo = model.createClass(NAMESPACE + "Mayo");
       mayo.addLabel("Mayo", null);
       mayo.addComment("County in Ireland",null);

       OntClass meath = model.createClass(NAMESPACE + "Meath");
       meath.addLabel("Meath", null);
       meath.addComment("County in Ireland",null);

       OntClass monaghan = model.createClass(NAMESPACE + "Monaghan");
       monaghan.addLabel("Monaghan", null);
       monaghan.addComment("County in Ireland",null);

       OntClass offaly = model.createClass(NAMESPACE + "Offaly");
       offaly.addLabel("Offaly", null);
       offaly.addComment("County in Ireland",null);

       OntClass roscommon = model.createClass(NAMESPACE + "Roscommon");
       roscommon.addLabel("Roscommon", null);
       roscommon.addComment("County in Ireland",null);

       OntClass sligo = model.createClass(NAMESPACE + "Sligo");
       sligo.addLabel("Sligo", null);
       sligo.addComment("County in Ireland",null);

       OntClass tipperary = model.createClass(NAMESPACE + "Tipperary");
       tipperary.addLabel("Tipperary", null);
       tipperary.addComment("County in Ireland",null);

       OntClass tyrone = model.createClass(NAMESPACE + "Tyrone");
       tyrone.addLabel("Tyrone", null);
       tyrone.addComment("County in Ireland",null);

       OntClass waterford = model.createClass(NAMESPACE + "Waterford");
       waterford.addLabel("Waterford", null);
       waterford.addComment("County in Ireland",null);

       OntClass westmeath = model.createClass(NAMESPACE + "Westmeath");
       westmeath.addLabel("Westmeath", null);
       westmeath.addComment("County in Ireland",null);

       OntClass wexford = model.createClass(NAMESPACE + "Wexford");
       wexford.addLabel("Wexford", null);
       wexford.addComment("County in Ireland",null);

       OntClass wicklow = model.createClass(NAMESPACE + "Wicklow");
       wicklow.addLabel("Wicklow", null);
       wicklow.addComment("County in Ireland",null);

       RDFList countyList = model.createList(new RDFNode[]{antrim,armagh,carlow,cavan,clare,cork,derry,donegal,down,
               dublin,fermanagh,galway,kerry,kildare,kilkenny,laois,leitrim,limerick,longford,louth,mayo,meath,
               monaghan,offaly,roscommon,sligo,tipperary,tyrone,waterford,westmeath,wexford,wicklow});

        OntClass countyName = model.createEnumeratedClass(NAMESPACE + "CountyName", countyList);
        countyName.addLabel("County Name", null);
        countyName.addComment("Ireland County Name", null);

        antrim.addSuperClass(countyName);
        armagh.addSuperClass(countyName);
        carlow.addSuperClass(countyName);
        cavan.addSuperClass(countyName);
        clare.addSuperClass(countyName);
        cork.addSuperClass(countyName);
        derry.addSuperClass(countyName);
        donegal.addSuperClass(countyName);
        down.addSuperClass(countyName);
        dublin.addSuperClass(countyName);
        fermanagh.addSuperClass(countyName);
        galway.addSuperClass(countyName);
        kerry.addSuperClass(countyName);
        kildare.addSuperClass(countyName);
        kilkenny.addSuperClass(countyName);
        laois.addSuperClass(countyName);
        leitrim.addSuperClass(countyName);
        limerick.addSuperClass(countyName);
        longford.addSuperClass(countyName);
        louth.addSuperClass(countyName);
        mayo.addSuperClass(countyName);
        meath.addSuperClass(countyName);
        monaghan.addSuperClass(countyName);
        offaly.addSuperClass(countyName);
        roscommon.addSuperClass(countyName);
        sligo.addSuperClass(countyName);
        tipperary.addSuperClass(countyName);
        tyrone.addSuperClass(countyName);
        waterford.addSuperClass(countyName);
        westmeath.addSuperClass(countyName);
        wexford.addSuperClass(countyName);
        wicklow.addSuperClass(countyName);


       //Grid Square Location Class
       OntClass gridSquareLocation = model.createClass(NAMESPACE + "GridSquareLocation");
       gridSquareLocation.addLabel("Grid Square Location", null);
       gridSquareLocation.addComment("Geographic Location using Eastings Northings notation" ,null);

       DatatypeProperty easting = model.createDatatypeProperty(NAMESPACE + "easting");
       easting.addLabel("easting", null);
       easting.addComment("Easting location", null);
       easting.setDomain(gridSquareLocation);
       easting.setRange(XSD.integer);
       gridSquareLocation.addSuperClass(model.createCardinalityRestriction(null, easting, 1));


       DatatypeProperty northing = model.createDatatypeProperty(NAMESPACE + "northing");
       northing.addLabel("northing", null);
       northing.addComment("Northing location", null);
       northing.setDomain(gridSquareLocation);
       northing.setRange(XSD.integer);
       gridSquareLocation.addSuperClass(model.createCardinalityRestriction(null, northing, 1));


        //Weather Record Class
       OntClass weatherRecord = model.createClass(NAMESPACE + "WeatherRecord");
       weatherRecord.addLabel("Weather Record", null);
       weatherRecord.addComment("Record Containing Weather Information.", null);


       //Temperature Record Class
       OntClass temperatureRecord = model.createClass(NAMESPACE + "TemperatureRecord");
       temperatureRecord.addLabel("Temperature Record", null);
       temperatureRecord.addComment("Weather record containing temperature information", null);
       temperatureRecord.addSuperClass(weatherRecord);

       DatatypeProperty temperatureMin = model.createDatatypeProperty(NAMESPACE + "temperatureMin");
       temperatureMin.addLabel("Temperature Minimum", null);
       temperatureMin.addComment("Minimum Temperature measured during timeframe", null);
       temperatureMin.setDomain(temperatureRecord);
       temperatureMin.setRange(XSD.xfloat);

       DatatypeProperty temperatureMax = model.createDatatypeProperty(NAMESPACE + "temperatureMax");
       temperatureMax.addLabel("Temperature Maximum", null);
       temperatureMax.addComment("Maximum Temperature measured during timeframe", null);
       temperatureMax.setDomain(temperatureRecord);
       temperatureMax.setRange(XSD.xfloat);

       DatatypeProperty temperatureMean = model.createDatatypeProperty(NAMESPACE + "temperatureMean");
       temperatureMean.addLabel("Temperature Mean", null);
       temperatureMean.addComment("Mean Temperature measured during timeframe", null);
       temperatureMean.setDomain(temperatureRecord);
       temperatureMean.setRange(XSD.xfloat);


       //Rainfall Record Class
       OntClass rainfallRecord = model.createClass(NAMESPACE + "RainfallRecord");
       rainfallRecord.addLabel("Rainfall Record", null);
       rainfallRecord.addComment("Weather record containing rainfall information", null);
       rainfallRecord.addSuperClass(weatherRecord);

       DatatypeProperty rainfall = model.createDatatypeProperty(NAMESPACE + "rainfall");
       temperatureMin.addLabel("rainfall", null);
       temperatureMin.addComment("Mean rainfall measured during timeframe", null);
       temperatureMin.setDomain(rainfallRecord);
       temperatureMin.setRange(XSD.integer);


       //Month Name Class
       OntClass january = model.createClass(NAMESPACE + "January");
       january.addLabel("January", null);
       january.addComment("Month Name", null);

       OntClass february = model.createClass(NAMESPACE + "February");
       february.addLabel("February", null);
       february.addComment("Month Name", null);

       OntClass march = model.createClass(NAMESPACE + "March");
       march.addLabel("March", null);
       march.addComment("Month Name", null);

       OntClass april = model.createClass(NAMESPACE + "April");
       april.addLabel("April", null);
       april.addComment("Month Name", null);

       OntClass may = model.createClass(NAMESPACE + "May");
       may.addLabel("May", null);
       may.addComment("Month Name", null);

       OntClass june = model.createClass(NAMESPACE + "June");
       june.addLabel("June", null);
       june.addComment("Month Name", null);

       OntClass july = model.createClass(NAMESPACE + "July");
       july.addLabel("July", null);
       july.addComment("Month Name", null);

       OntClass august = model.createClass(NAMESPACE + "August");
       august.addLabel("August", null);
       august.addComment("Month Name", null);

       OntClass september = model.createClass(NAMESPACE + "September");
       september.addLabel("September", null);
       september.addComment("Month Name", null);

       OntClass october = model.createClass(NAMESPACE + "October");
       october.addLabel("October", null);
       october.addComment("Month Name", null);

       OntClass november = model.createClass(NAMESPACE + "Novemeber");
       november.addLabel("November", null);
       november.addComment("Month Name", null);

       OntClass december = model.createClass(NAMESPACE + "December");
       december.addLabel("December", null);
       december.addComment("Month Name", null);

       RDFList monthList = model.createList(new RDFNode[]{january,february,march,april,may,june,july,august,september,
               october,november,december});
       OntClass monthName = model.createEnumeratedClass(NAMESPACE + "MonthName", monthList);
       monthName.addLabel("Month Name", null);
       monthName.addComment("Name of Month", null);

       january.addSuperClass(monthName);
       february.addSuperClass(monthName);
       march.addSuperClass(monthName);
       april.addSuperClass(monthName);
       may.addSuperClass(monthName);
       june.addSuperClass(monthName);
       july.addSuperClass(monthName);
       august.addSuperClass(monthName);
       september.addSuperClass(monthName);
       october.addSuperClass(monthName);
       november.addSuperClass(monthName);
       december.addSuperClass(monthName);


       //InterClass Relationships
       TransitiveProperty higherThan = model.createTransitiveProperty(NAMESPACE + "higherThan");
       higherThan.addLabel("Higher Than",null);
       higherThan.addComment("List other stations which this station is higher than.", null);
       higherThan.setDomain(weatherStation);
       higherThan.setRange(weatherStation);

       InverseFunctionalProperty lowerThan = model.createInverseFunctionalProperty(NAMESPACE + "lowerThan");
       lowerThan.addLabel("Lower Than", null);
       lowerThan.addComment("List other stations which this station is lower than.", null);
       lowerThan.addInverseOf(higherThan);
       lowerThan.setDomain(weatherStation);
       lowerThan.setRange(weatherStation);

       DatatypeProperty locatedAt = model.createDatatypeProperty(NAMESPACE + "locatedAt");
       locatedAt.addLabel("Located at", null);
       locatedAt.addComment("Specifies a geographical Location", null);
       locatedAt.setDomain(weatherStation);
       locatedAt.setRange(latLongLocation);
       locatedAt.setRange(gridSquareLocation);

       DatatypeProperty locatedIn = model.createDatatypeProperty(NAMESPACE + "LocatedIn");
       locatedIn.addLabel("Located In", null);
       locatedIn.addComment("Specifies if a lat long location is located in a County", null);
       locatedIn.setDomain(latLongLocation);
       locatedIn.setRange(county);
       latLongLocation.addSuperClass(model.createCardinalityRestriction(null, locatedIn, 1));

       DatatypeProperty countyIsNamed = model.createDatatypeProperty(NAMESPACE + "countyIsNamed");
       countyIsNamed.addLabel("county is named", null);
       countyIsNamed.addComment("Specifies the name of a County", null);
       countyIsNamed.setDomain(county);
       countyIsNamed.setRange(countyName);
       county.addSuperClass(model.createCardinalityRestriction(null, countyIsNamed, 1));

       SymmetricProperty adjacentTo = model.createSymmetricProperty(NAMESPACE + "adjacentTo");
       adjacentTo.addLabel("adjacent to", null);
       adjacentTo.addComment("Specifies if a grid square is adjacent to another grid square.", null);
       adjacentTo.setDomain(gridSquareLocation);
       adjacentTo.setRange(gridSquareLocation);

       DatatypeProperty rainedAt = model.createDatatypeProperty(NAMESPACE + "rainedAt");
       rainedAt.addLabel("rained at", null);
       rainedAt.addComment("Specifies the location of a Weather Record", null);
       rainedAt.setDomain(weatherRecord);
       rainedAt.setRange(gridSquareLocation);
       weatherRecord.addSuperClass(model.createCardinalityRestriction(null, rainedAt, 1));

       DatatypeProperty monthRained = model.createDatatypeProperty(NAMESPACE + "monthRained");
       monthRained.addLabel("month rained", null);
       monthRained.addComment("Specifies the month of a Weather Record", null);
       monthRained.setDomain(weatherRecord);
       monthRained.setRange(monthName);
       weatherRecord.addSuperClass(model.createCardinalityRestriction(null, monthRained, 1));


       writeToFile(model);
   }

    public static void writeToFile(OntModel ontModel) {
        try {
            ontModel.write(new FileWriter(ONTOLOGY_PATH), "TURTLE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
