public class Main {
    public static void main(String[] args)
    {
        System.out.println("Creating Ontology");
        OntologyMaker.createOntology();
        System.out.println("Ontology Created");
        Window window = new Window();
        window.launchWindow();
    }
}
