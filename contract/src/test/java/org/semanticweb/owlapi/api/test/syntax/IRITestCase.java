package org.semanticweb.owlapi.api.test.syntax;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.rdf4j.rio.helpers.JSONLDSettings;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.api.test.baseclasses.TestBase;
import org.semanticweb.owlapi.apitest.TestFiles;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.ManchesterSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.NQuadsDocumentFormat;
import org.semanticweb.owlapi.formats.NTriplesDocumentFormat;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.formats.RDFJsonDocumentFormat;
import org.semanticweb.owlapi.formats.RDFJsonLDDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.formats.RioRDFXMLDocumentFormat;
import org.semanticweb.owlapi.formats.RioTurtleDocumentFormat;
import org.semanticweb.owlapi.formats.TrigDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.io.UnparsableOntologyException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLRuntimeException;

class IRITestCase extends TestBase {

    @Test
    void shouldParseIRIAndSkipPrefixedSpaceManchesterSyntax() {
        roundTrip(new ManchesterSyntaxDocumentFormat(), TestFiles.BAD_MANCHESTER);
    }

    @Test
    void shouldParseIRIAndSkipPrefixedSpaceTriG() {
        // no crash on this version
        loadFrom(TestFiles.BAD_TRIG, new TrigDocumentFormat());
    }

    protected void shouldFail(String bad, OWLDocumentFormat f2) {
        assertThrows(OWLRuntimeException.class, () -> loadFrom(bad, f2));
    }

    @Test
    void shouldOutputNamedGraph() {
        IRI iri = iri("urn:test:", "ontology");
        OWLOntology o = create(iri);
        o.getOWLOntologyManager().getOntologyConfigurator().withNamedGraphIRIEnabled(true);
        StringDocumentTarget saved = saveOntology(o, new TrigDocumentFormat());
        assertTrue(saved.toString().contains(iri.toQuotedString() + " {"));
    }

    @Test
    void shouldOutputOverriddenNamedGraph() {
        OWLDocumentFormat format = new TrigDocumentFormat();
        String value = "urn:test:onto";
        format.setParameter("namedGraphOverride", value);
        OWLOntology o = create(iri("urn:test:", "ontology"));
        StringDocumentTarget saved = saveOntology(o, format);
        assertTrue(saved.toString().contains("<" + value + ">"));
    }

    @Test
    void shouldParseIRIAndSkipPrefixedSpaceRDFJSON() {
        roundTrip(new RDFJsonDocumentFormat(), TestFiles.BAD_RDFJSON);
    }

    @Test
    void shouldParseIRIAndSkipPrefixedSpaceNTriples() {
        assertThrowsWithCause(OWLRuntimeException.class, UnparsableOntologyException.class,
            () -> roundTrip(new NTriplesDocumentFormat(), TestFiles.BAD_NTRIPLES));
    }

    @Test
    void shouldParseIRIAndSkipPrefixedSpaceRioRDFXML() {
        // no crash on this version
        loadFrom(TestFiles.BAD_RIO_RDF, new RioRDFXMLDocumentFormat());
    }

    @Test
    void shouldParseIRIAndSkipPrefixedSpaceRioTurtle() {
        // no crash on this version
        loadFrom(TestFiles.BAD_RIO_TURTLE, new RioTurtleDocumentFormat());
    }

    @Test
    void shouldNotParseIRIAndSkipPrefixedSpaceJSONLD() {
        // The correct behaviour for JSON-LD 1.1 is to skip invalid data, 
        // including IRIs that start with a space. A warning should be logged. 
        // To get an exception instead you can set JSONLDSettings.EXCEPTION_ON_WARNING.
        // This test shows how to do that.
        OWLDocumentFormat format = new RDFJsonLDDocumentFormat();
        format.setParameter(JSONLDSettings.EXCEPTION_ON_WARNING, Boolean.TRUE);
        assertThrows(OWLRuntimeException.class, () -> roundTrip(format, TestFiles.BAD_JSON_LD));
    }

    @Test
    void shouldParseIRIAndSkipPrefixedSpaceJSONLD() throws OWLOntologyCreationException {
        // The correct behaviour for JSON-LD 1.1 is to skip invalid data, 
        // including IRIs that start with a space. A warning should be logged. 
        // To get an exception instead you can set JSONLDSettings.EXCEPTION_ON_WARNING.
        // This test uses the default behaviour and shows which invalid data is ignored.
        OWLDocumentFormat format = new RDFJsonLDDocumentFormat();
        OWLOntology o = loadFrom(TestFiles.BAD_JSON_LD, format);
        // Ignoring incorrect data, the ontology contains only a declaration axiom
        OWLOntology o2 = m1.createOntology(o.getOntologyID().getOntologyIRI()
            .get());
        o2.add(df.getOWLDeclarationAxiom(df.getOWLAnnotationProperty(
            "http://x.org/myprop")));
        equal(o, o2);
    }

    @Test
    void shouldParseIRIAndSkipPrefixedSpaceNQUADS() {
        assertThrowsWithCause(OWLRuntimeException.class, UnparsableOntologyException.class,
            () -> roundTrip(new NQuadsDocumentFormat(), TestFiles.BAD_NTRIPLES));
    }

    @Test
    void shouldParseIRIAndSkipPrefixedSpaceOWLXML() {
        roundTrip(new OWLXMLDocumentFormat(), TestFiles.BAD_OWLXML);
    }

    @Test
    void shouldParseIRIAndSkipPrefixedSpaceRDFXML() {
        shouldFail(TestFiles.BAD_RDFXML, new RDFXMLDocumentFormat());
    }

    @Test
    void shouldParseIRIAndSkipPrefixedSpaceTurtle() {
        shouldFail(TestFiles.BAD_TURTLE, new TurtleDocumentFormat());
    }

    @Test
    void shouldParseIRIAndSkipPrefixedSpaceFunctionalSyntax() {
        roundTrip(new FunctionalSyntaxDocumentFormat(), TestFiles.BAD_FUNCTIONAL);
    }

    protected void roundTrip(OWLDocumentFormat format, String bad) {
        String good = bad.replace(" https://example.org/bad-url", "https://example.org/bad-url");
        OWLOntology o1 = loadFrom(bad, format);
        OWLOntology o2 = loadFrom(good, format);
        equal(o1, o2);
    }
}
