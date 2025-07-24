/*******************************************************************************
 * In addition to the license file in this project, this file is also
 * Copyright (c) 2015 Eclipse RDF4J contributors, Aduna, and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 *******************************************************************************/
package com.rakensi.basex.xquery.functions.rdf;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.basex.query.QueryException;
import org.basex.query.QueryModule;
import org.basex.query.func.java.JavaCall;
import org.basex.query.value.node.ANode;
import org.eclipse.rdf4j.common.xml.XMLUtil;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.base.CoreDatatype;
import org.eclipse.rdf4j.model.util.Literals;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

public class RioParserModule extends QueryModule
{

  @Requires(Permission.NONE)
  @Deterministic
  @ContextDependent
  public org.basex.query.value.Value parse(final String uri) throws QueryException
  {
    RDFFormat format = Rio.getParserFormatForFileName(uri).orElse(null);
    if (format == null) {
      throw new QueryException("Unable to determine RDF format for " + uri);
    }
    return parse(uri, format);
  }

  @Requires(Permission.NONE)
  @Deterministic
  @ContextDependent
  public org.basex.query.value.Value parseRdfXml(final String uri) throws QueryException
  {
    return parse(uri, RDFFormat.RDFXML);
  }

  @Requires(Permission.NONE)
  @Deterministic
  @ContextDependent
  public org.basex.query.value.Value parseNTriples(final String uri) throws QueryException
  {
    return parse(uri, RDFFormat.NTRIPLES);
  }

  @Requires(Permission.NONE)
  @Deterministic
  @ContextDependent
  public org.basex.query.value.Value parseTtl(final String uri) throws QueryException
  {
    return parse(uri, RDFFormat.TURTLE);
  }

  @Requires(Permission.NONE)
  @Deterministic
  @ContextDependent
  public org.basex.query.value.Value parseTtlStar(final String uri) throws QueryException
  {
    return parse(uri, RDFFormat.TURTLESTAR);
  }

  @Requires(Permission.NONE)
  @Deterministic
  @ContextDependent
  public org.basex.query.value.Value parseN3(final String uri) throws QueryException
  {
    return parse(uri, RDFFormat.N3);
  }

  @Requires(Permission.NONE)
  @Deterministic
  @ContextDependent
  public org.basex.query.value.Value parseTrix(final String uri) throws QueryException
  {
    return parse(uri, RDFFormat.TRIX);
  }

  @Requires(Permission.NONE)
  @Deterministic
  @ContextDependent
  public org.basex.query.value.Value parseTrig(final String uri) throws QueryException
  {
    return parse(uri, RDFFormat.TRIG);
  }

  @Requires(Permission.NONE)
  @Deterministic
  @ContextDependent
  public org.basex.query.value.Value parseTrigStar(final String uri) throws QueryException
  {
    return parse(uri, RDFFormat.TRIGSTAR);
  }

  @Requires(Permission.NONE)
  @Deterministic
  @ContextDependent
  public org.basex.query.value.Value parseBinary(final String uri) throws QueryException
  {
    return parse(uri, RDFFormat.BINARY);
  }

  @Requires(Permission.NONE)
  @Deterministic
  @ContextDependent
  public org.basex.query.value.Value parseNQuads(final String uri) throws QueryException
  {
    return parse(uri, RDFFormat.NQUADS);
  }

  @Requires(Permission.NONE)
  @Deterministic
  @ContextDependent
  public org.basex.query.value.Value parseJsonLd(final String uri) throws QueryException
  {
    return parse(uri, RDFFormat.JSONLD);
  }

  @Requires(Permission.NONE)
  @Deterministic
  @ContextDependent
  public org.basex.query.value.Value parseNdJsonLd(final String uri) throws QueryException
  {
    return parse(uri, RDFFormat.NDJSONLD);
  }

  @Requires(Permission.NONE)
  @Deterministic
  @ContextDependent
  public org.basex.query.value.Value parseRdfJson(final String uri) throws QueryException
  {
    return parse(uri, RDFFormat.RDFJSON);
  }

  @Requires(Permission.NONE)
  @Deterministic
  @ContextDependent
  public org.basex.query.value.Value parseRdfA(final String uri) throws QueryException
  {
    return parse(uri, RDFFormat.RDFA);
  }

  @Requires(Permission.NONE)
  @Deterministic
  @ContextDependent
  public org.basex.query.value.Value parseHdt(final String uri) throws QueryException
  {
    return parse(uri, RDFFormat.HDT);
  }


  /**
   * Parse an RDF document from a URI using the specified RDF format.
   * @param uri the URI of the RDF document to parse.
   * @param format the RDF format to use for parsing.
   * @return a BaseX value representing the parsed RDF document.
   * @throws QueryException if an error occurs during parsing.
   */
  public org.basex.query.value.Value parse(final String uri, RDFFormat format) throws QueryException
  {
    try {
      URL documentURL = URI.create(uri).toURL();
      RDFParser rdfParser = Rio.createParser(format);
      InputStream inputStream = documentURL.openStream();
      BaseXRDFHandler rdfHandler = new BaseXRDFHandler();
      rdfParser.setRDFHandler(rdfHandler);
      try {
        rdfParser.parse(inputStream, documentURL.toString());
      } finally {
        inputStream.close();
      }
      Document rdfDocument = rdfHandler.getRdfDocument();
      ANode bxOutputDocument = (ANode)JavaCall.toValue(rdfDocument, queryContext, null);
      return bxOutputDocument;
    } catch (Exception e) {
      throw new QueryException(e);
    }
  }


  class BaseXRDFHandler extends AbstractRDFHandler {
    private static final String XMLNS_NAMESPACE = "http://www.w3.org/2000/xmlns/";

    private final Map<String, String> namespaceTable; // Map URIs to prefixes.
    //private final ValueFactory vf;
    private Document rdfDocument;
    private Element rootElement;
    private String defaultNamespace;

    public BaseXRDFHandler() {
      this.namespaceTable = new LinkedHashMap<>();
      //this.vf = SimpleValueFactory.getInstance();
    }

    public Document getRdfDocument() {
      return rdfDocument;
    }

    @Override
    public void startRDF() throws RDFHandlerException {
      namespaceTable.clear();
      namespaceTable.put(RDF.NAMESPACE, "rdf"); // Add the default RDF namespace.
      try
      {
        rdfDocument = DOMImplementationRegistry.newInstance().getDOMImplementation("XML 3.0").createDocument(null, null, null);
        rootElement = rdfDocument.createElementNS(RDF.NAMESPACE, "rdf:RDF");
        rootElement.setAttributeNS(XMLNS_NAMESPACE, "xmlns:rdf", RDF.NAMESPACE);
      }
      catch (Exception e)
      {
        throw new RDFHandlerException("Failed to create RDF document.", e);
      }
    }

    @Override
    public void endRDF() throws RDFHandlerException {
      rdfDocument.appendChild(rootElement);
    }

    @Override
    public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
      if (prefix.isEmpty()) {
        defaultNamespace = uri;
        return;
      }
      if (namespaceTable.containsKey(uri)) {
        return;
      }
        boolean isLegalPrefix = XMLUtil.isNCName(prefix);
        if (!isLegalPrefix || namespaceTable.containsValue(prefix)) {
          // Generate a legal unique prefix.
          if (!isLegalPrefix) {
            prefix = "ns";
          }
          int number = 1;
          while (namespaceTable.containsValue(prefix + number)) {
            number++;
          }
          prefix += number;
        }
        namespaceTable.put(uri, prefix);
    }

    @Override
    public void handleStatement(Statement st) throws RDFHandlerException {
      Resource subj = st.getSubject();
      IRI pred = st.getPredicate();
      Value obj = st.getObject();

      // Verify that an XML namespace-qualified name can be created for the predicate.
      String predString = pred.toString();
      int predSplitIdx = XMLUtil.findURISplitIndex(predString);
      if (predSplitIdx == -1) {
        throw new RDFHandlerException("Unable to create XML namespace-qualified name for predicate: " + predString);
      }
      String predNamespace = predString.substring(0, predSplitIdx);
      String predLocalName = predString.substring(predSplitIdx);

      // Create the element for the statement.
      Element statementElement = rdfDocument.createElementNS(RDF.NAMESPACE, "rdf:Description");
      if (subj instanceof BNode) {
        statementElement.setAttributeNS(RDF.NAMESPACE, "rdf:nodeID", getValidNodeId((BNode) subj));
      } else {
        IRI subjUri = (IRI) subj;
        statementElement.setAttributeNS(RDF.NAMESPACE, "rdf:about", subjUri.toString());
      }
      rootElement.appendChild(statementElement);

      // Create the element for the predicate.
      String nsPrefix = namespaceTable.get(predNamespace);
      Element predicateElement = rdfDocument.createElementNS(predNamespace, nsPrefix+":"+predLocalName);
      predicateElement.setAttributeNS(XMLNS_NAMESPACE, "xmlns:" + nsPrefix, predNamespace);
      statementElement.appendChild(predicateElement);

      // Handle the object of the statement.
      if (obj instanceof Resource) {
        Resource objRes = (Resource) obj;
        if (objRes instanceof BNode) {
          predicateElement.setAttributeNS(RDF.NAMESPACE, "rdf:nodeID", getValidNodeId((BNode) objRes));
        } else {
          IRI objUri = (IRI) objRes;
          predicateElement.setAttributeNS(RDF.NAMESPACE, "rdf:resource", objUri.toString());
        }
      } else if (obj instanceof Literal) {
        Literal objLit = (Literal) obj;
        boolean isXMLLiteral = false;
        if (Literals.isLanguageLiteral(objLit)) {
          predicateElement.setAttribute("xml:lang", objLit.getLanguage().get());
        } else {
          CoreDatatype coreDatatype = objLit.getCoreDatatype();
          // Check if datatype is rdf:XMLLiteral.
          isXMLLiteral = coreDatatype == CoreDatatype.RDF.XMLLITERAL;
          if (isXMLLiteral) {
            predicateElement.setAttributeNS(RDF.NAMESPACE, "rdf:parseType", "Literal");
          } else if (coreDatatype != CoreDatatype.XSD.STRING) {
            predicateElement.setAttributeNS(RDF.NAMESPACE, "rdf:datatype", objLit.getDatatype().toString());
          }
          // Set the content of the predicate element.
          if (isXMLLiteral) {
            // Add the XML literal as XML content.
            //TODO This is wrong, but we do not have a way to handle XML literals in RDF/XML.
            predicateElement.setTextContent(objLit.getLabel());
          } else {
            predicateElement.setTextContent(objLit.getLabel());
          }
        }
      }

    }

    @Override
    public void handleComment(String comment) throws RDFHandlerException {
      rootElement.appendChild(rdfDocument.createComment(comment));
    }


    /**
     * Create a syntactically valid node id from the supplied blank node id. This is necessary because RDF/XML syntax
     * enforces the blank node id is a valid NCName.
     * @param bNode a blank node identifier
     * @return the blank node identifier converted to a form that is a valid NCName.
     * @see <a href="http://www.w3.org/TR/REC-rdf-syntax/#rdf-id">section 7.2.34 of the RDF/XML Syntax specification</a>
     */
    private String getValidNodeId(BNode bNode) {
      String validNodeId = bNode.getID();
      if (!XMLUtil.isNCName(validNodeId)) {
        StringBuilder builder = new StringBuilder();
        if (validNodeId.isEmpty()) {
          builder.append("genid-hash-");
          builder.append(Integer.toHexString(System.identityHashCode(bNode)));
        } else {
          if (!XMLUtil.isNCNameStartChar(validNodeId.charAt(0))) {
            // prepend legal start char
            builder.append("genid-start-");
            builder.append(Integer.toHexString(validNodeId.charAt(0)));
          } else {
            builder.append(validNodeId.charAt(0));
          }
          for (int i = 1; i < validNodeId.length(); i++) {
            // Do char-by-char scan and replace illegal chars where necessary.
            if (XMLUtil.isNCNameChar(validNodeId.charAt(i))) {
              builder.append(validNodeId.charAt(i));
            } else {
              // Replace incompatible char with encoded hex value that will always be alphanumeric.
              builder.append(Integer.toHexString(validNodeId.charAt(i)));
            }
          }
        }
        validNodeId = builder.toString();
      }
      return validNodeId;
    }

  }

}
