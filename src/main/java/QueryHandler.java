import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;

import javax.xml.transform.Result;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class QueryHandler {
    private OntModel ontModel;
    private String[] sparqlQueries;

    public QueryHandler() throws IOException {
        ontModel = ModelFactory.createOntologyModel();
        ontModel.read("ontologyFile.ttl");

        File directory = new File(getClass().getResource("sparql_queries").getFile());
        File[] files = directory.listFiles();

        Arrays.sort(files);
        sparqlQueries = new String[files.length];

        for (int i = 0; i < files.length; i++) {
            sparqlQueries[i] = new String(Files.readAllBytes(Paths.get(files[i].getPath())), StandardCharsets.UTF_8);
        }

    }

//    public String execute(int index)
//    {
//        String question = sparqlQueries[index];
//        Query query = QueryFactory.create(question);
//        QueryExecution execution = QueryExecutionFactory.create(query, ontModel);
//        ResultSet resultSet = execution.execSelect();
//        String result = ResultSetFormatter.asText(resultSet);
//        execution.close();
//        return result;
//    }

    public String executeQuery(int idx) {
        String questionTxt = sparqlQueries[idx];
        return queryWithTxt(questionTxt);
    }

    private String queryWithTxt(String queryString) {
        Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, ontModel);
//        QueryExecution queryExecutionFactory = QueryExecutionFactory.create(queryString, ontModel);
        ResultSet results = queryExecution.execSelect();

        String resultTxt = ResultSetFormatter.asText(results);
        queryExecution.close();

        return resultTxt;
    }
}
