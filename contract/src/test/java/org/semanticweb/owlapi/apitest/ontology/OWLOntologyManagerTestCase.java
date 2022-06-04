/* This file is part of the OWL API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright 2014, The University of Manchester
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */
package org.semanticweb.owlapi.apitest.ontology;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.semanticweb.owlapi.apitest.baseclasses.TestBase;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyAlreadyExistsException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyDocumentAlreadyExistsException;
import org.semanticweb.owlapi.utility.SimpleIRIMapper;

/**
 * @author Matthew Horridge, The University of Manchester, Information
 *         Management Group
 * @since 3.0.0
 */
public class OWLOntologyManagerTestCase extends TestBase {

    private static final String NS = "http://www.semanticweb.org/ontologies/ontology";

    @Test
    public void testCreateAnonymousOntology() {
        OWLOntology ontology = getAnonymousOWLOntology();
        assertNotNull(ontology);
        assertNotNull(ontology.getOntologyID());
        assertFalse(ontology.getOntologyID().getDefaultDocumentIRI().isPresent());
        assertFalse(ontology.getOntologyID().getOntologyIRI().isPresent());
        assertFalse(ontology.getOntologyID().getVersionIRI().isPresent());
        assertNotNull(m.getOntologyDocumentIRI(ontology));
    }

    @Test
    public void testCreateOntologyWithIRI() {
        IRI ontologyIRI = df.getNextDocumentIRI(NS);
        OWLOntology ontology = getOWLOntology(ontologyIRI);
        assertNotNull(ontology);
        assertNotNull(ontology.getOntologyID());
        assertEquals(ontologyIRI, ontology.getOntologyID().getDefaultDocumentIRI().get());
        assertEquals(ontologyIRI, ontology.getOntologyID().getOntologyIRI().get());
        assertFalse(ontology.getOntologyID().getVersionIRI().isPresent());
        assertEquals(ontologyIRI, m.getOntologyDocumentIRI(ontology));
    }

    @Test
    public void testCreateOntologyWithIRIAndVersionIRI() {
        IRI ontologyIRI = df.getNextDocumentIRI(NS);
        IRI versionIRI = df.getNextDocumentIRI("http://www.semanticweb.org/ontologies/ontology/version");
        OWLOntology ontology = getOWLOntology(df.getOWLOntologyID(ontologyIRI, versionIRI));
        assertNotNull(ontology);
        assertNotNull(ontology.getOntologyID());
        assertEquals(versionIRI, ontology.getOntologyID().getDefaultDocumentIRI().get());
        assertEquals(ontologyIRI, ontology.getOntologyID().getOntologyIRI().get());
        assertEquals(versionIRI, ontology.getOntologyID().getVersionIRI().get());
        assertEquals(versionIRI, m.getOntologyDocumentIRI(ontology));
    }

    @Test
    public void testCreateOntologyWithIRIWithMapper() {
        IRI ontologyIRI = df.getNextDocumentIRI(NS);
        IRI documentIRI = df.getNextDocumentIRI("file:documentIRI");
        SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyIRI, documentIRI);
        m.getIRIMappers().add(mapper);
        OWLOntology ontology = getOWLOntology(ontologyIRI);
        assertNotNull(ontology);
        assertNotNull(ontology.getOntologyID());
        assertEquals(ontologyIRI, ontology.getOntologyID().getDefaultDocumentIRI().get());
        assertEquals(ontologyIRI, ontology.getOntologyID().getOntologyIRI().get());
        assertFalse(ontology.getOntologyID().getVersionIRI().isPresent());
        assertEquals(documentIRI, m.getOntologyDocumentIRI(ontology));
    }

    @Test
    public void testCreateOntologyWithIRIAndVersionIRIWithMapper() {
        IRI ontologyIRI = df.getNextDocumentIRI(NS);
        IRI versionIRI = df.getNextDocumentIRI("http://www.semanticweb.org/ontologies/ontology/version");
        IRI documentIRI = df.getNextDocumentIRI("file:documentIRI");
        SimpleIRIMapper mapper = new SimpleIRIMapper(versionIRI, documentIRI);
        m.getIRIMappers().add(mapper);
        OWLOntology ontology = getOWLOntology(df.getOWLOntologyID(ontologyIRI, versionIRI));
        assertNotNull(ontology);
        assertNotNull(ontology.getOntologyID());
        assertEquals(versionIRI, ontology.getOntologyID().getDefaultDocumentIRI().get());
        assertEquals(ontologyIRI, ontology.getOntologyID().getOntologyIRI().get());
        assertEquals(versionIRI, ontology.getOntologyID().getVersionIRI().get());
        assertEquals(documentIRI, m.getOntologyDocumentIRI(ontology));
    }

    @Test(expected = OWLOntologyAlreadyExistsException.class)
    public void testCreateDuplicateOntologyWithIRI() throws OWLOntologyCreationException {
        IRI ontologyIRI = df.getNextDocumentIRI(NS);
        m.createOntology(ontologyIRI);
        m.createOntology(ontologyIRI);
    }

    @Test(expected = OWLOntologyAlreadyExistsException.class)
    public void testCreateDuplicateOntologyWithIRIAndVersionIRI() throws OWLOntologyCreationException {
        IRI ontologyIRI = df.getNextDocumentIRI(NS);
        IRI versionIRI = df.getNextDocumentIRI(NS);
        m.createOntology(df.getOWLOntologyID(ontologyIRI, versionIRI));
        m.createOntology(df.getOWLOntologyID(ontologyIRI, versionIRI));
    }

    @Test(expected = OWLOntologyDocumentAlreadyExistsException.class)
    public void testCreateDuplicatedDocumentIRI() throws OWLOntologyCreationException {
        IRI ontologyIRI = df.getNextDocumentIRI(NS);
        IRI ontologyIRI2 = df.getNextDocumentIRI("http://www.semanticweb.org/ontologies/ontology2");
        IRI documentIRI = df.getNextDocumentIRI("file:documentIRI");
        m.getIRIMappers().add(new SimpleIRIMapper(ontologyIRI, documentIRI));
        m.getIRIMappers().add(new SimpleIRIMapper(ontologyIRI2, documentIRI));
        m.createOntology(df.getOWLOntologyID(ontologyIRI));
        m.createOntology(df.getOWLOntologyID(ontologyIRI2));
    }
}