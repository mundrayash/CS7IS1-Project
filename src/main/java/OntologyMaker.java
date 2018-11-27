import com.esri.core.geometry.*;
import org.apache.commons.io.FileUtils;
import org.apache.jena.atlas.io.IO;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;
import org.apache.jena.riot.RDFDataMgr;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class OntologyMaker {
    private final static String URI_BASE = "http://www.fake.com/cs7is1-project";
    private final static String NAMESPACE = URI_BASE + "#";
    private final static String ONTOLOGY_PATH = "files/ontologyFile.ttl";
    private static final String COUNTY_URL = "http://data.geohive.ie/dumps/county/default.ttl";
    private static final String COUNTY_PATH = "files/county.ttl";
    private static final String STATION_URL = "https://cli.fusio.net/cli/climate_data/webdata/StationDetails.csv";
    private static final String STATION_PATH = "files/stations.csv";
    private static final String TEMPERATURE_PATH = "files/temperatures.zip";
    private static final String TEMPERATURE_URL = "https://www.met.ie/climate-ireland/IE_TEMP_8110_V2.zip";
    private static final String MIN_TEMPERATURE_PATH = "files/IE_TN_8110_V2.txt";
    private static final String MAX_TEMPERATURE_PATH = "files/IE_TX_8110_V2.txt";
    private static final String MEAN_TEMPERATURE_PATH = "files/IE_TMEAN_8110_V2.txt";
    private static final String RAINFALL_PATH = "files/rainfall.zip";
    private static final String RAINFALL_URL = "https://www.met.ie/climate-ireland/IE_RR_8110_V1.zip";
    private static final String MEAN_RAINFALL_PATH = "files/IE_RR_8110_V1.txt";



    private static void unzip(String zippedFile, String destDirerectory) throws IOException {
        File destDir = new File(destDirerectory);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zippedFile));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    private static void createFiles() {
        Path countyPath = Paths.get(COUNTY_PATH);
        URI countyURL = URI.create(COUNTY_URL);
        Path stationPath = Paths.get(STATION_PATH);
        URI stationURL = URI.create(STATION_URL);
        Path rainfallPath = Paths.get(RAINFALL_PATH);
        URI rainifallURL = URI.create(RAINFALL_URL);
        Path temperaturePath = Paths.get(TEMPERATURE_PATH);
        URI temperatureURL = URI.create(TEMPERATURE_URL);

        System.out.println("Checking for necessary files...");
        if (!Files.exists(countyPath)) {
            try {
                System.out.println("County Borders file not found...");
                Files.createDirectories(countyPath.getParent());
                System.out.println("Downloading from " + COUNTY_URL);
                try (InputStream in = countyURL.toURL().openStream()) {
                    Files.copy(in, countyPath);
                }
                System.out.println("File downloaded and stored in " + COUNTY_PATH);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!Files.exists(stationPath)) {
            try {
                System.out.println("Weather Stations file not found...");
                Files.createDirectories(countyPath.getParent());
                System.out.println("Downloading from " + STATION_URL);
                try (InputStream in = stationURL.toURL().openStream()) {
                    Files.copy(in, stationPath);
                }
                System.out.println("File downloaded and stored in " + STATION_PATH);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!Files.exists(rainfallPath)) {
            try {
                System.out.println("Rainfall file not found...");
                Files.createDirectories(countyPath.getParent());
                System.out.println("Downloading from " + RAINFALL_URL);
                try (InputStream in = rainifallURL.toURL().openStream()) {
                    Files.copy(in, Paths.get("files/rainfall.zip"));
                }
                System.out.println("File downloaded and stored in " + RAINFALL_PATH);
                unzip(RAINFALL_PATH, "files");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!Files.exists(temperaturePath)) {
            try {
                System.out.println("Temperature file not found...");
                Files.createDirectories(temperaturePath.getParent());
                System.out.println("Downloading from " + TEMPERATURE_URL);
                try (InputStream in = temperatureURL.toURL().openStream()) {
                    Files.copy(in, Paths.get(TEMPERATURE_PATH));
                }
                System.out.println("File downloaded and stored in " + TEMPERATURE_PATH);
                unzip(TEMPERATURE_PATH, "files");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void createOntology() {
        createFiles();

        OntModel model = ModelFactory.createOntologyModel();
        model.setNsPrefix("base", NAMESPACE);

        Ontology ontology = model.createOntology(URI_BASE);
        ontology.addLabel("ireland-weather-station-county", null);


        ontology.addProperty(DCTerms.creator, "Patrick Meleady");
        ontology.addProperty(DCTerms.creator, "Yash Mundra");
        ontology.addProperty(DCTerms.creator, "Caitriona O'Driscoll");
        ontology.addProperty(DCTerms.creator, "Stephen Kelehan");
        ontology.addProperty(DCTerms.title, "CS7IS1 Project");

        String description = "Ireland weather station information, with county information along with rainfall and temperature information." +
                "Weather Stations have several properties including open year, close year and height. They also have " +
                "properties named LocatedIn which provides location information in two seperate formats namely Latitude and" +
                "Longitude and Eastings Northings format.";

        ontology.addProperty(DCTerms.description, description);

        //Weather Station Class
        OntClass weatherStation = model.createClass(NAMESPACE + "WeatherStation");
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
        openYear.setLabel("open year", null);
        openYear.setComment("Opening Year of Weather Station", null);
        openYear.setDomain(weatherStation);
        openYear.setRange(XSD.integer);
        weatherStation.addSuperClass(model.createCardinalityRestriction(null, openYear, 1));


        DatatypeProperty closeYear = model.createDatatypeProperty(NAMESPACE + "closeYear");
        closeYear.setLabel("close year", null);
        closeYear.setComment("Closing Year of Weather Station", null);
        closeYear.setDomain(weatherStation);
        closeYear.setRange(XSD.integer);
        weatherStation.addSuperClass(model.createMaxCardinalityRestriction(null, closeYear, 1));

        //Location Class
        OntClass location = model.createClass(NAMESPACE + "Location");
        location.addLabel("Location", null);
        location.addComment("Superclass for different Location formats",null);

        //Latitude Longitude Location Class
        OntClass latLongLocation = model.createClass(NAMESPACE + "LatLongLocation");
        latLongLocation.addLabel("Latitude Longitude Location", null);
        latLongLocation.addComment("Geographic Location using Latitude and Longitude", null);
        latLongLocation.addSuperClass(location);

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
        antrim.addComment("County in Ireland", null);

        OntClass armagh = model.createClass(NAMESPACE + "Armagh");
        armagh.addLabel("Armagh", null);
        armagh.addComment("County in Ireland", null);

        OntClass carlow = model.createClass(NAMESPACE + "Carlow");
        carlow.addLabel("Carlow", null);
        carlow.addComment("County in Ireland", null);

        OntClass cavan = model.createClass(NAMESPACE + "Cavan");
        cavan.addLabel("Cavan", null);
        cavan.addComment("County in Ireland", null);

        OntClass clare = model.createClass(NAMESPACE + "Clare");
        clare.addLabel("Clare", null);
        clare.addComment("County in Ireland", null);

        OntClass cork = model.createClass(NAMESPACE + "Cork");
        cork.addLabel("Cork", null);
        cork.addComment("County in Ireland", null);

        OntClass derry = model.createClass(NAMESPACE + "Derry");
        derry.addLabel("Derry", null);
        derry.addComment("County in Ireland", null);

        OntClass donegal = model.createClass(NAMESPACE + "Donegal");
        donegal.addLabel("Donegal", null);
        donegal.addComment("County in Ireland", null);

        OntClass down = model.createClass(NAMESPACE + "Down");
        down.addLabel("Down", null);
        down.addComment("County in Ireland", null);

        OntClass dublin = model.createClass(NAMESPACE + "Dublin");
        dublin.addLabel("Dublin", null);
        dublin.addComment("County in Ireland", null);

        OntClass fermanagh = model.createClass(NAMESPACE + "Fermanagh");
        fermanagh.addLabel("Fermanagh", null);
        fermanagh.addComment("County in Ireland", null);

        OntClass galway = model.createClass(NAMESPACE + "Galway");
        galway.addLabel("Galway", null);
        galway.addComment("County in Ireland", null);

        OntClass kerry = model.createClass(NAMESPACE + "Kerry");
        kerry.addLabel("Kerry", null);
        kerry.addComment("County in Ireland", null);

        OntClass kildare = model.createClass(NAMESPACE + "Kildare");
        kildare.addLabel("Kildare", null);
        kildare.addComment("County in Ireland", null);

        OntClass kilkenny = model.createClass(NAMESPACE + "Kilkenny");
        kilkenny.addLabel("Kilkenny", null);
        kilkenny.addComment("County in Ireland", null);

        OntClass laois = model.createClass(NAMESPACE + "Laois");
        laois.addLabel("Laois", null);
        laois.addComment("County in Ireland", null);

        OntClass leitrim = model.createClass(NAMESPACE + "Leitrim");
        leitrim.addLabel("Leitrim", null);
        leitrim.addComment("County in Ireland", null);

        OntClass limerick = model.createClass(NAMESPACE + "Limerick");
        limerick.addLabel("Limerick", null);
        limerick.addComment("County in Ireland", null);

        OntClass longford = model.createClass(NAMESPACE + "Longford");
        longford.addLabel("Longford", null);
        longford.addComment("County in Ireland", null);

        OntClass louth = model.createClass(NAMESPACE + "Louth");
        louth.addLabel("Louth", null);
        louth.addComment("County in Ireland", null);

        OntClass mayo = model.createClass(NAMESPACE + "Mayo");
        mayo.addLabel("Mayo", null);
        mayo.addComment("County in Ireland", null);

        OntClass meath = model.createClass(NAMESPACE + "Meath");
        meath.addLabel("Meath", null);
        meath.addComment("County in Ireland", null);

        OntClass monaghan = model.createClass(NAMESPACE + "Monaghan");
        monaghan.addLabel("Monaghan", null);
        monaghan.addComment("County in Ireland", null);

        OntClass offaly = model.createClass(NAMESPACE + "Offaly");
        offaly.addLabel("Offaly", null);
        offaly.addComment("County in Ireland", null);

        OntClass roscommon = model.createClass(NAMESPACE + "Roscommon");
        roscommon.addLabel("Roscommon", null);
        roscommon.addComment("County in Ireland", null);

        OntClass sligo = model.createClass(NAMESPACE + "Sligo");
        sligo.addLabel("Sligo", null);
        sligo.addComment("County in Ireland", null);

        OntClass tipperary = model.createClass(NAMESPACE + "Tipperary");
        tipperary.addLabel("Tipperary", null);
        tipperary.addComment("County in Ireland", null);

        OntClass tyrone = model.createClass(NAMESPACE + "Tyrone");
        tyrone.addLabel("Tyrone", null);
        tyrone.addComment("County in Ireland", null);

        OntClass waterford = model.createClass(NAMESPACE + "Waterford");
        waterford.addLabel("Waterford", null);
        waterford.addComment("County in Ireland", null);

        OntClass westmeath = model.createClass(NAMESPACE + "Westmeath");
        westmeath.addLabel("Westmeath", null);
        westmeath.addComment("County in Ireland", null);

        OntClass wexford = model.createClass(NAMESPACE + "Wexford");
        wexford.addLabel("Wexford", null);
        wexford.addComment("County in Ireland", null);

        OntClass wicklow = model.createClass(NAMESPACE + "Wicklow");
        wicklow.addLabel("Wicklow", null);
        wicklow.addComment("County in Ireland", null);

        RDFList countyList = model.createList(new RDFNode[]{antrim, armagh, carlow, cavan, clare, cork, derry, donegal, down,
                dublin, fermanagh, galway, kerry, kildare, kilkenny, laois, leitrim, limerick, longford, louth, mayo, meath,
                monaghan, offaly, roscommon, sligo, tipperary, tyrone, waterford, westmeath, wexford, wicklow});

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
        gridSquareLocation.addComment("Geographic Location using Eastings Northings notation", null);
        gridSquareLocation.addSuperClass(location);

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
        rainfall.addLabel("rainfall", null);
        rainfall.addComment("Mean rainfall measured during timeframe", null);
        rainfall.setDomain(rainfallRecord);
        rainfall.setRange(XSD.integer);


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

        RDFList monthList = model.createList(new RDFNode[]{january, february, march, april, may, june, july, august, september,
                october, november, december});
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
        higherThan.addLabel("Higher Than", null);
        higherThan.addComment("List other stations which this station is higher than.", null);
        higherThan.setDomain(weatherStation);
        higherThan.setRange(weatherStation);

        InverseFunctionalProperty lowerThan = model.createInverseFunctionalProperty(NAMESPACE + "lowerThan");
        lowerThan.addLabel("Lower Than", null);
        lowerThan.addComment("List other stations which this station is lower than.", null);
        lowerThan.addInverseOf(higherThan);
        lowerThan.setDomain(weatherStation);
        lowerThan.setRange(weatherStation);

        ObjectProperty locatedAt = model.createObjectProperty(NAMESPACE + "locatedAt");
        locatedAt.addLabel("Located at", null);
        locatedAt.addComment("Specifies a geographical Location", null);
        locatedAt.setDomain(weatherStation);
        locatedAt.setRange(location);

        ObjectProperty locatedIn = model.createObjectProperty(NAMESPACE + "LocatedIn");
        locatedIn.addLabel("Located In", null);
        locatedIn.addComment("Specifies if a lat long location is located in a County", null);
        locatedIn.setDomain(latLongLocation);
        locatedIn.setRange(county);
        latLongLocation.addSuperClass(model.createCardinalityRestriction(null, locatedIn, 1));

        ObjectProperty countyIsNamed = model.createObjectProperty(NAMESPACE + "countyIsNamed");
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

        ObjectProperty happenedAt = model.createObjectProperty(NAMESPACE + "happenedAt");
        happenedAt.addLabel("happened at", null);
        happenedAt.addComment("Specifies the location of a Weather Record", null);
        happenedAt.setDomain(weatherRecord);
        happenedAt.setRange(gridSquareLocation);
        weatherRecord.addSuperClass(model.createCardinalityRestriction(null, happenedAt, 1));

        ObjectProperty monthHappened = model.createObjectProperty(NAMESPACE + "monthHappened");
        monthHappened.addLabel("month happened", null);
        monthHappened.addComment("Specifies the month of a Weather Record", null);
        monthHappened.setDomain(weatherRecord);
        monthHappened.setRange(monthName);
        weatherRecord.addSuperClass(model.createCardinalityRestriction(null, monthHappened, 1));




        ///Create Individuals

        Model countyModel = RDFDataMgr.loadModel(COUNTY_PATH);

        ResIterator countyIter = countyModel.listResourcesWithProperty(RDFS.label);

        Property hasGeometry = countyModel.getProperty("http://www.opengis.net/ont/geosparql#hasGeometry");
        Property asWKT = countyModel.getProperty("http://www.opengis.net/ont/geosparql#asWKT");

        ArrayList<Geometry> countyGeometries = new ArrayList<>();
        ArrayList<Individual> countyIndividuals = new ArrayList<>();

        int individualStationID = 1;
        int individualGridSquareID = 1;
        int individualLatLongID = 1;

        while (countyIter.hasNext()) {
            Resource countyRDF = countyIter.next();

            // labels
            NodeIterator labelsIter = countyModel.listObjectsOfProperty(countyRDF, RDFS.label);
            List<RDFNode> labels = labelsIter.toList();
            String idLabel = "";
            String gaLabel = "";
            String enLabel = "";

            for (RDFNode label : labels) {
                Literal name = label.asLiteral();
                if (name.getLanguage().equals("ga")) {
                    gaLabel = name.getString();
                } else if (name.getLanguage().equals("en")) {
                    enLabel = name.getString();
                } else {
                    idLabel = name.getString();
                }
            }

            // WKT
            Resource geoResource = countyModel.listObjectsOfProperty(countyRDF, hasGeometry).next().asResource();
            String wkt = countyModel.listObjectsOfProperty(geoResource, asWKT).next().toString();
            wkt = wkt.substring(0, wkt.indexOf("^^"));
            OperatorImportFromWkt importer = OperatorImportFromWkt.local();
            Geometry geometry = importer.execute(WktImportFlags.wktImportDefaults, Geometry.Type.Unknown, wkt, null);
            countyGeometries.add(geometry);

            ArrayList<Object> info = new ArrayList<>();
            info.add(idLabel);
            info.add(enLabel);
            info.add(gaLabel);
            info.add(geometry);
            float scale = 7365.0f;
            info.add((float) geometry.calculateArea2D() * scale);
            Individual aCounty = county.createIndividual(NAMESPACE + info.get(0));
            if(idLabel.equals("ANTRIM")) {
                aCounty.addProperty(countyIsNamed, antrim);
            }
            else if(idLabel.equals("ARMAGH")) {
                aCounty.addProperty(countyIsNamed, armagh);
            }
            else if(idLabel.equals("CARLOW")) {
                aCounty.addProperty(countyIsNamed, carlow);
            }
            else if(idLabel.equals("CAVAN")) {
                aCounty.addProperty(countyIsNamed, cavan);
            }
            else if(idLabel.equals("CLARE")) {
                aCounty.addProperty(countyIsNamed, clare);
            }
            else if(idLabel.equals("CORK")) {
                aCounty.addProperty(countyIsNamed, cork);
            }
            else if(idLabel.equals("DERRY")) {
                aCounty.addProperty(countyIsNamed, derry);
            }
            else if(idLabel.equals("DONEGAL")) {
                aCounty.addProperty(countyIsNamed, donegal);
            }
            else if(idLabel.equals("DOWN")) {
                aCounty.addProperty(countyIsNamed, down);
            }
            else if(idLabel.equals("DUBLIN")) {
                aCounty.addProperty(countyIsNamed, dublin);
            }
            else if(idLabel.equals("FERMANAGH")) {
                aCounty.addProperty(countyIsNamed, fermanagh);
            }
            else if(idLabel.equals("GALWAY")) {
                aCounty.addProperty(countyIsNamed, galway);
            }
            else if(idLabel.equals("KERRY")) {
                aCounty.addProperty(countyIsNamed, kerry);
            }
            else if(idLabel.equals("KILDARE")) {
                aCounty.addProperty(countyIsNamed, kildare);
            }
            else if(idLabel.equals("KILKENNY")) {
                aCounty.addProperty(countyIsNamed, kilkenny);
            }
            else if(idLabel.equals("LAOIS")) {
                aCounty.addProperty(countyIsNamed, laois);
            }
            else if(idLabel.equals("LEITRIM")) {
                aCounty.addProperty(countyIsNamed, leitrim);
            }
            else if(idLabel.equals("LIMERICK")) {
                aCounty.addProperty(countyIsNamed, limerick);
            }
            else if(idLabel.equals("LONGFORD")) {
                aCounty.addProperty(countyIsNamed, longford);
            }
            else if(idLabel.equals("LOUTH")) {
                aCounty.addProperty(countyIsNamed, louth);
            }
            else if(idLabel.equals("MAYO")) {
                aCounty.addProperty(countyIsNamed, mayo);
            }
            else if(idLabel.equals("MEATH")) {
                aCounty.addProperty(countyIsNamed, meath);
            }
            else if(idLabel.equals("MONAGHAN")) {
                aCounty.addProperty(countyIsNamed, monaghan);
            }
            else if(idLabel.equals("OFFALY")) {
                aCounty.addProperty(countyIsNamed, offaly);
            }
            else if(idLabel.equals("ROSCOMMON")) {
                aCounty.addProperty(countyIsNamed, roscommon);
            }
            else if(idLabel.equals("SLIGO")) {
                aCounty.addProperty(countyIsNamed, sligo);
            }
            else if(idLabel.equals("TIPPERARY")) {
                aCounty.addProperty(countyIsNamed, tipperary);
            }
            else if(idLabel.equals("TYRONE")) {
                aCounty.addProperty(countyIsNamed, tyrone);
            }
            else if(idLabel.equals("WATERFORD")) {
                aCounty.addProperty(countyIsNamed, waterford);
            }
            else if(idLabel.equals("WESTMEATH")) {
                aCounty.addProperty(countyIsNamed, westmeath);
            }
            else if(idLabel.equals("WEXFORD")) {
                aCounty.addProperty(countyIsNamed, wexford);
            }
            else if(idLabel.equals("WICKLOW")) {
                aCounty.addProperty(countyIsNamed, wicklow);
            }

            aCounty.addLabel((String) info.get(0), null);
            aCounty.addLabel((String) info.get(1), "en");
            aCounty.addLabel((String) info.get(2), "ga");
            aCounty.addLiteral(area, (float) info.get(4));
            countyIndividuals.add(aCounty);
        }
        System.out.println("Finished Parsing Geohive DataSet");

        ArrayList<Individual> latLongList = new ArrayList<>();
        ArrayList<Individual> gridSquareList = new ArrayList<>();
        try {
            FileReader in = new FileReader(STATION_PATH);
            CSVParser weatherStationCSV = CSVFormat.DEFAULT.parse(in);
            List<CSVRecord> records = weatherStationCSV.getRecords();
            records.remove(0);
            records.remove(0);

            ArrayList<Individual> weatherStationList = new ArrayList<>();

            for (CSVRecord record : records) {
                int aStationID = Integer.parseInt(record.get(1));
                String aStationName = record.get(2);
                int aStationHeight = Integer.parseInt(record.get(3));
                int aEasting = Integer.parseInt(record.get(4));
                int aNorthing = Integer.parseInt(record.get(5));
                float aLatitude = Float.parseFloat(record.get(6));
                float aLongitude = Float.parseFloat(record.get(7));
                int aOpenYear = Integer.parseInt(record.get(8));
                int aCloseYear;
                if(!record.get(9).equals("")) {
                    aCloseYear = Integer.parseInt(record.get(9));
                }
                else{
                    aCloseYear = -1;
                }

                Individual aGridSquare = null;
                boolean existingGridSquareFound = false;
                int i = 0;
                while(i < gridSquareList.size() && !existingGridSquareFound)
                {
                    Individual gridSq = gridSquareList.get(i);
                    if(gridSq.getProperty(easting).getInt()==aEasting && gridSq.getProperty(northing).getInt() == aNorthing){
                        aGridSquare = gridSq;
                        existingGridSquareFound = true;
                    }
                    else{
                        i++;
                    }
                }
                if(!existingGridSquareFound){
                    aGridSquare = gridSquareLocation.createIndividual(NAMESPACE + "GridSquare" + individualGridSquareID);
                    individualGridSquareID++;
                    aGridSquare.addLiteral(easting, aEasting);
                    aGridSquare.addLiteral(northing, aNorthing);
                    gridSquareList.add(aGridSquare);
                }

                Individual aLatLongLocation = null;
                boolean existingLatLongFound = false;
                i = 0;
                while(i < latLongList.size() && !existingLatLongFound)
                {
                    Individual latLong = latLongList.get(i);
                    if(latLong.getProperty(latitude).getFloat()==aLatitude && latLong.getProperty(longitude).getFloat() == aLongitude){
                        aLatLongLocation = latLong;
                        existingLatLongFound = true;
                    }
                    else{
                        i++;
                    }
                }
                if(!existingLatLongFound){
                    aLatLongLocation = latLongLocation.createIndividual(NAMESPACE + "LatLong" + individualLatLongID);
                    individualLatLongID++;
                    aLatLongLocation.addLiteral(latitude, aLatitude);
                    aLatLongLocation.addLiteral(longitude, aLongitude);
                    latLongList.add(aLatLongLocation);
                }


                boolean containingCountyFound = false;
                i = 0;
                Point stationLocation = new Point(aLongitude, aLatitude);
                while(i < countyGeometries.size() && !containingCountyFound){

                    Geometry currentGeometry = countyGeometries.get(i);
                    OperatorWithin within = OperatorWithin.local();
                    if (within.execute(stationLocation, currentGeometry, SpatialReference.create("WGS84"), null)) {
                        aLatLongLocation.addProperty(locatedIn, countyList.get(i));
                        containingCountyFound = true;
                    }
                    else{
                        i++;
                    }
                }

                Individual aWeatherStation = weatherStation.createIndividual(NAMESPACE + "Station"+individualStationID);
                individualStationID++;
                aWeatherStation.addLiteral(stationNumber, aStationID);
                aWeatherStation.addLiteral(stationName, aStationName);
                aWeatherStation.addLiteral(height, aStationHeight);
                aWeatherStation.addLiteral(openYear, aOpenYear);
                if(aCloseYear != -1){
                    aWeatherStation.addLiteral(closeYear, aCloseYear);
                }

                aWeatherStation.addProperty(locatedAt, aGridSquare);
                aWeatherStation.addProperty(locatedAt, aLatLongLocation);

                weatherStationList.add(aWeatherStation);
            }

            for(int i =0; i < weatherStationList.size() ; i++){
                Individual weatherStationI = weatherStationList.get(i);
                for(int j = i+1; j < weatherStationList.size(); j++){
                    Individual weatherStationJ = weatherStationList.get(j);

                    if(weatherStationI.getProperty(height).getInt() > weatherStationJ.getProperty(height).getInt()){
                        weatherStationI.addProperty(higherThan, weatherStationJ);
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("Finished Parsing Weather Station Dataset");

        try {
            FileReader in = new FileReader(MEAN_RAINFALL_PATH);
            CSVParser rainfallCSV = CSVFormat.DEFAULT.parse(in);
            List<CSVRecord> records = rainfallCSV.getRecords();
            records.remove(0);
            int rainRecordID = 1;

            for(CSVRecord record : records)
            {
                int aEasting = Integer.parseInt(record.get(0));
                int aNorthing = Integer.parseInt(record.get(1));
                int janRainfall = Integer.parseInt(record.get(2));
                int febRainfall = Integer.parseInt(record.get(3));
                int marRainfall = Integer.parseInt(record.get(4));
                int aprRainfall = Integer.parseInt(record.get(5));
                int mayRainfall = Integer.parseInt(record.get(6));
                int junRainfall = Integer.parseInt(record.get(7));
                int julRainfall = Integer.parseInt(record.get(8));
                int augRainfall = Integer.parseInt(record.get(9));
                int sepRainfall = Integer.parseInt(record.get(10));
                int octRainfall = Integer.parseInt(record.get(11));
                int novRainfall = Integer.parseInt(record.get(12));
                int decRainfall = Integer.parseInt(record.get(13));

                boolean validLocation = false;
                Individual currentLocation = null;
                Individual adjacentLocation = null;

                int i = 0;
                while(i < gridSquareList.size() && !validLocation)
                {
                    Individual currentGridSquare = gridSquareList.get(i);
                    if(currentGridSquare.getProperty(easting).getInt()==aEasting && currentGridSquare.getProperty(northing).getInt() == aNorthing) {
                        currentLocation = currentGridSquare;
                        validLocation = true;
                    }
                    else if(Math.abs(currentGridSquare.getProperty(easting).getInt()-aEasting)<1100 || Math.abs(currentGridSquare.getProperty(northing).getInt() - aNorthing)<1100){
                        adjacentLocation = currentGridSquare;
                        validLocation = true;
                    }
                    else{
                        i++;
                    }
                }
                if(validLocation){
                    if(currentLocation == null) {
                        currentLocation = gridSquareLocation.createIndividual(NAMESPACE + "GridSquare" + individualGridSquareID);
                        individualGridSquareID++;
                        currentLocation.addLiteral(easting, aEasting);
                        currentLocation.addLiteral(northing, aNorthing);
                        currentLocation.addProperty(adjacentTo, adjacentLocation);
                    }
                    Individual janRainfallRecord = rainfallRecord.createIndividual(NAMESPACE + "RainRecord" + rainRecordID);
                    rainRecordID++;
                    janRainfallRecord.addLiteral(rainfall, janRainfall);
                    janRainfallRecord.addProperty(monthHappened, january);
                    janRainfallRecord.addProperty(happenedAt, currentLocation);

                    Individual febRainfallRecord = rainfallRecord.createIndividual(NAMESPACE + "RainRecord" + rainRecordID);
                    rainRecordID++;
                    febRainfallRecord.addLiteral(rainfall, febRainfall);
                    febRainfallRecord.addProperty(monthHappened, february);
                    febRainfallRecord.addProperty(happenedAt, currentLocation);

                    Individual marRainfallRecord = rainfallRecord.createIndividual(NAMESPACE + "RainRecord" + rainRecordID);
                    rainRecordID++;
                    marRainfallRecord.addLiteral(rainfall, marRainfall);
                    marRainfallRecord.addProperty(monthHappened, march);
                    marRainfallRecord.addProperty(happenedAt, currentLocation);

                    Individual aprRainfallRecord = rainfallRecord.createIndividual(NAMESPACE + "RainRecord" + rainRecordID);
                    rainRecordID++;
                    aprRainfallRecord.addLiteral(rainfall, aprRainfall);
                    aprRainfallRecord.addProperty(monthHappened, april);
                    aprRainfallRecord.addProperty(happenedAt, currentLocation);

                    Individual mayRainfallRecord = rainfallRecord.createIndividual(NAMESPACE + "RainRecord" + rainRecordID);
                    rainRecordID++;
                    mayRainfallRecord.addLiteral(rainfall, mayRainfall);
                    mayRainfallRecord.addProperty(monthHappened, may);
                    mayRainfallRecord.addProperty(happenedAt, currentLocation);

                    Individual junRainfallRecord = rainfallRecord.createIndividual(NAMESPACE + "RainRecord" + rainRecordID);
                    rainRecordID++;
                    junRainfallRecord.addLiteral(rainfall, junRainfall);
                    junRainfallRecord.addProperty(monthHappened, june);
                    junRainfallRecord.addProperty(happenedAt, currentLocation);

                    Individual julRainfallRecord = rainfallRecord.createIndividual(NAMESPACE + "RainRecord" + rainRecordID);
                    rainRecordID++;
                    julRainfallRecord.addLiteral(rainfall, julRainfall);
                    julRainfallRecord.addProperty(monthHappened, july);
                    julRainfallRecord.addProperty(happenedAt, currentLocation);

                    Individual augRainfallRecord = rainfallRecord.createIndividual(NAMESPACE + "RainRecord" + rainRecordID);
                    rainRecordID++;
                    augRainfallRecord.addLiteral(rainfall,augRainfall);
                    augRainfallRecord.addProperty(monthHappened, august);
                    augRainfallRecord.addProperty(happenedAt, currentLocation);

                    Individual sepRainfallRecord = rainfallRecord.createIndividual(NAMESPACE + "RainRecord" + rainRecordID);
                    rainRecordID++;
                    sepRainfallRecord.addLiteral(rainfall, sepRainfall);
                    sepRainfallRecord.addProperty(monthHappened, september);
                    sepRainfallRecord.addProperty(happenedAt, currentLocation);

                    Individual octRainfallRecord = rainfallRecord.createIndividual(NAMESPACE + "RainRecord" + rainRecordID);
                    rainRecordID++;
                    octRainfallRecord.addLiteral(rainfall, octRainfall);
                    octRainfallRecord.addProperty(monthHappened, october);
                    octRainfallRecord.addProperty(happenedAt, currentLocation);

                    Individual novRainfallRecord = rainfallRecord.createIndividual(NAMESPACE + "RainRecord" + rainRecordID);
                    rainRecordID++;
                    novRainfallRecord.addLiteral(rainfall, novRainfall);
                    novRainfallRecord.addProperty(monthHappened, november);
                    novRainfallRecord.addProperty(happenedAt, currentLocation);

                    Individual decRainfallRecord = rainfallRecord.createIndividual(NAMESPACE + "RainRecord" + rainRecordID);
                    rainRecordID++;
                    decRainfallRecord.addLiteral(rainfall, decRainfall);
                    decRainfallRecord.addProperty(monthHappened, december);
                    decRainfallRecord.addProperty(happenedAt, currentLocation);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("Finished Parsing Rainfall Dataset");

        try {
            FileReader in = new FileReader(MAX_TEMPERATURE_PATH);
            CSVParser maxTemperatureCSV = CSVFormat.DEFAULT.parse(in);
            List<CSVRecord> maxTempRecords = maxTemperatureCSV.getRecords();
            in = new FileReader(MIN_TEMPERATURE_PATH);
            CSVParser minTemperatureCSV = CSVFormat.DEFAULT.parse(in);
            List<CSVRecord> minTempRecords = minTemperatureCSV.getRecords();
            in = new FileReader(MEAN_TEMPERATURE_PATH);
            CSVParser meanTemperatureCSV = CSVFormat.DEFAULT.parse(in);
            List<CSVRecord> meanTempRecords = meanTemperatureCSV.getRecords();
            maxTempRecords.remove(0);
            minTempRecords.remove(0);
            meanTempRecords.remove(0);
            int tempRecordID = 1;

            for(int i = 0 ; i< maxTempRecords.size(); i++)
            {
                CSVRecord maxTempRecord = maxTempRecords.get(i);
                CSVRecord minTempRecord = minTempRecords.get(i);
                CSVRecord meanTempRecord = meanTempRecords.get(i);

                int aEasting =   Integer.parseInt(maxTempRecord.get(0));
                int aNorthing =  Integer.parseInt(maxTempRecord.get(1));

                float janMaxTemp = Float.parseFloat(maxTempRecord.get(2));
                float janMinTemp = Float.parseFloat(minTempRecord.get(2));
                float janMeanTemp =Float.parseFloat(meanTempRecord.get(2));

                float febMaxTemp = Float.parseFloat(maxTempRecord.get(3));
                float febMinTemp = Float.parseFloat(minTempRecord.get(3));
                float febMeanTemp =Float.parseFloat(meanTempRecord.get(3));

                float marMaxTemp = Float.parseFloat(maxTempRecord.get(4));
                float marMinTemp = Float.parseFloat(minTempRecord.get(4));
                float marMeanTemp =Float.parseFloat(meanTempRecord.get(4));

                float aprMaxTemp = Float.parseFloat(maxTempRecord.get(5));
                float aprMinTemp = Float.parseFloat(minTempRecord.get(5));
                float aprMeanTemp =Float.parseFloat(meanTempRecord.get(5));

                float mayMaxTemp = Float.parseFloat(maxTempRecord.get(6));
                float mayMinTemp = Float.parseFloat(minTempRecord.get(6));
                float mayMeanTemp =Float.parseFloat(meanTempRecord.get(6));

                float junMaxTemp = Float.parseFloat(maxTempRecord.get(7));
                float junMinTemp = Float.parseFloat(minTempRecord.get(7));
                float junMeanTemp =Float.parseFloat(meanTempRecord.get(7));

                float julMaxTemp = Float.parseFloat(maxTempRecord.get(8));
                float julMinTemp = Float.parseFloat(minTempRecord.get(8));
                float julMeanTemp =Float.parseFloat(meanTempRecord.get(8));

                float augMaxTemp = Float.parseFloat(maxTempRecord.get(9));
                float augMinTemp = Float.parseFloat(minTempRecord.get(9));
                float augMeanTemp =Float.parseFloat(meanTempRecord.get(9));

                float sepMaxTemp = Float.parseFloat(maxTempRecord.get(10));
                float sepMinTemp = Float.parseFloat(minTempRecord.get(10));
                float sepMeanTemp =Float.parseFloat(meanTempRecord.get(10));

                float octMaxTemp = Float.parseFloat(maxTempRecord.get(11));
                float octMinTemp = Float.parseFloat(minTempRecord.get(11));
                float octMeanTemp =Float.parseFloat(meanTempRecord.get(11));

                float novMaxTemp = Float.parseFloat(maxTempRecord.get(12));
                float novMinTemp = Float.parseFloat(minTempRecord.get(12));
                float novMeanTemp =Float.parseFloat(meanTempRecord.get(12));

                float decMaxTemp = Float.parseFloat(maxTempRecord.get(13));
                float decMinTemp = Float.parseFloat(minTempRecord.get(13));
                float decMeanTemp =Float.parseFloat(meanTempRecord.get(13));


                Individual currentLocation = null;

                boolean validLocation = false;
                int j = 0;
                while(j < gridSquareList.size() && !validLocation)
                {
                    Individual currentGridSquare = gridSquareList.get(j);
                    if(currentGridSquare.getProperty(easting).getInt()==aEasting && currentGridSquare.getProperty(northing).getInt() == aNorthing) {
                        currentLocation = currentGridSquare;
                        validLocation = true;
                    }
                    else{
                        j++;
                    }
                }
                if(validLocation){

                    Individual janTempRecord = temperatureRecord.createIndividual(NAMESPACE + "TemperatureRecord" + tempRecordID);
                    tempRecordID++;
                    janTempRecord.addLiteral(temperatureMax, janMaxTemp);
                    janTempRecord.addLiteral(temperatureMean, janMeanTemp);
                    janTempRecord.addLiteral(temperatureMin, janMinTemp);
                    janTempRecord.addProperty(happenedAt, currentLocation);
                    janTempRecord.addProperty(monthHappened, january);

                    Individual febTempRecord = temperatureRecord.createIndividual(NAMESPACE + "TemperatureRecord" + tempRecordID);
                    tempRecordID++;
                    febTempRecord.addLiteral(temperatureMax, febMaxTemp);
                    febTempRecord.addLiteral(temperatureMean, febMeanTemp);
                    febTempRecord.addLiteral(temperatureMin, febMinTemp);
                    febTempRecord.addProperty(happenedAt, currentLocation);
                    febTempRecord.addProperty(monthHappened, february);

                    Individual marTempRecord = temperatureRecord.createIndividual(NAMESPACE + "TemperatureRecord" + tempRecordID);
                    tempRecordID++;
                    marTempRecord.addLiteral(temperatureMax, marMaxTemp);
                    marTempRecord.addLiteral(temperatureMean, marMeanTemp);
                    marTempRecord.addLiteral(temperatureMin, marMinTemp);
                    marTempRecord.addProperty(happenedAt, currentLocation);
                    marTempRecord.addProperty(monthHappened, march);

                    Individual aprTempRecord = temperatureRecord.createIndividual(NAMESPACE + "TemperatureRecord" + tempRecordID);
                    tempRecordID++;
                    aprTempRecord.addLiteral(temperatureMax, aprMaxTemp);
                    aprTempRecord.addLiteral(temperatureMean, aprMeanTemp);
                    aprTempRecord.addLiteral(temperatureMin, aprMinTemp);
                    aprTempRecord.addProperty(happenedAt, currentLocation);
                    aprTempRecord.addProperty(monthHappened, april);

                    Individual mayTempRecord = temperatureRecord.createIndividual(NAMESPACE + "TemperatureRecord" + tempRecordID);
                    tempRecordID++;
                    mayTempRecord.addLiteral(temperatureMax, mayMaxTemp);
                    mayTempRecord.addLiteral(temperatureMean, mayMeanTemp);
                    mayTempRecord.addLiteral(temperatureMin, mayMinTemp);
                    mayTempRecord.addProperty(happenedAt, currentLocation);
                    mayTempRecord.addProperty(monthHappened, may);

                    Individual junTempRecord = temperatureRecord.createIndividual(NAMESPACE + "TemperatureRecord" + tempRecordID);
                    tempRecordID++;
                    junTempRecord.addLiteral(temperatureMax, junMaxTemp);
                    junTempRecord.addLiteral(temperatureMean, junMeanTemp);
                    junTempRecord.addLiteral(temperatureMin, junMinTemp);
                    junTempRecord.addProperty(happenedAt, currentLocation);
                    junTempRecord.addProperty(monthHappened, june);

                    Individual julTempRecord = temperatureRecord.createIndividual(NAMESPACE + "TemperatureRecord" + tempRecordID);
                    tempRecordID++;
                    julTempRecord.addLiteral(temperatureMax, julMaxTemp);
                    julTempRecord.addLiteral(temperatureMean, julMeanTemp);
                    julTempRecord.addLiteral(temperatureMin, julMinTemp);
                    julTempRecord.addProperty(happenedAt, currentLocation);
                    julTempRecord.addProperty(monthHappened, july);

                    Individual augTempRecord = temperatureRecord.createIndividual(NAMESPACE + "TemperatureRecord" + tempRecordID);
                    tempRecordID++;
                    augTempRecord.addLiteral(temperatureMax, augMaxTemp);
                    augTempRecord.addLiteral(temperatureMean, augMeanTemp);
                    augTempRecord.addLiteral(temperatureMin, augMinTemp);
                    augTempRecord.addProperty(happenedAt, currentLocation);
                    augTempRecord.addProperty(monthHappened, august);

                    Individual sepTempRecord = temperatureRecord.createIndividual(NAMESPACE + "TemperatureRecord" + tempRecordID);
                    tempRecordID++;
                    sepTempRecord.addLiteral(temperatureMax, sepMaxTemp);
                    sepTempRecord.addLiteral(temperatureMean, sepMeanTemp);
                    sepTempRecord.addLiteral(temperatureMin, sepMinTemp);
                    sepTempRecord.addProperty(happenedAt, currentLocation);
                    sepTempRecord.addProperty(monthHappened, september);

                    Individual octTempRecord = temperatureRecord.createIndividual(NAMESPACE + "TemperatureRecord" + tempRecordID);
                    tempRecordID++;
                    octTempRecord.addLiteral(temperatureMax, octMaxTemp);
                    octTempRecord.addLiteral(temperatureMean, octMeanTemp);
                    octTempRecord.addLiteral(temperatureMin, octMinTemp);
                    octTempRecord.addProperty(happenedAt, currentLocation);
                    octTempRecord.addProperty(monthHappened, october);

                    Individual novTempRecord = temperatureRecord.createIndividual(NAMESPACE + "TemperatureRecord" + tempRecordID);
                    tempRecordID++;
                    novTempRecord.addLiteral(temperatureMax, novMaxTemp);
                    novTempRecord.addLiteral(temperatureMean, novMeanTemp);
                    novTempRecord.addLiteral(temperatureMin, novMinTemp);
                    novTempRecord.addProperty(happenedAt, currentLocation);
                    novTempRecord.addProperty(monthHappened, november);

                    Individual decTempRecord = temperatureRecord.createIndividual(NAMESPACE + "TemperatureRecord" + tempRecordID);
                    tempRecordID++;
                    decTempRecord.addLiteral(temperatureMax, decMaxTemp);
                    decTempRecord.addLiteral(temperatureMean, decMeanTemp);
                    decTempRecord.addLiteral(temperatureMin, decMinTemp);
                    decTempRecord.addProperty(happenedAt, currentLocation);
                    decTempRecord.addProperty(monthHappened, december);

                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("Finished Parsing Temperature Datasets");

        writeToFile(model);

    }

    private static void writeToFile(OntModel ontModel) {
        try {
            ontModel.write(new FileWriter(ONTOLOGY_PATH), "TURTLE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
