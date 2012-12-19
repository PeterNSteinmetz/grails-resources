package org.grails.plugin.resource.util;

import grails.test.GrailsUnitTestCase

class DispositionsUtilitiesTests extends GrailsUnitTestCase {
	void testAddingDispositionToRequest() {
		def request = [:]
		assertTrue DispositionsUtilities.getRequestDispositionsRemaining(request).empty

		DispositionsUtilities.addDispositionToRequest(request, 'head', 'dummy')
		assertTrue((['head'] as Set) == DispositionsUtilities.getRequestDispositionsRemaining(request))

		// Let's just make sure its a set
		DispositionsUtilities.addDispositionToRequest(request, 'head', 'dummy')
		assertTrue((['head'] as Set) == DispositionsUtilities.getRequestDispositionsRemaining(request))

		DispositionsUtilities.addDispositionToRequest(request, 'defer', 'dummy')
		assertTrue((['head', 'defer'] as Set) == DispositionsUtilities.getRequestDispositionsRemaining(request))

		DispositionsUtilities.addDispositionToRequest(request, 'image', 'dummy')
		assertTrue((['head', 'image', 'defer'] as Set) == DispositionsUtilities.getRequestDispositionsRemaining(request))
	}

	void testRemovingDispositionFromRequest() {
		def request = [(DispositionsUtilities.REQ_ATTR_DISPOSITIONS_REMAINING):(['head', 'image', 'defer'] as Set)]

		assertTrue((['head', 'image', 'defer'] as Set) == DispositionsUtilities.getRequestDispositionsRemaining(request))

		DispositionsUtilities.removeDispositionFromRequest(request, 'head')
		assertTrue((['defer', 'image'] as Set) == DispositionsUtilities.getRequestDispositionsRemaining(request))

		DispositionsUtilities.removeDispositionFromRequest(request, 'defer')
		assertTrue((['image'] as Set) == DispositionsUtilities.getRequestDispositionsRemaining(request))
	}

}
