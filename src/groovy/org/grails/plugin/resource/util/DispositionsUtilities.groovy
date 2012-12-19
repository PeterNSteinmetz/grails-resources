package org.grails.plugin.resource.util

/**
 * Utility class for handling dispositions in ServletRequests.
 * 
 * @author Peter N. Steinmetz
 */
class DispositionsUtilities {
	static REQ_ATTR_DEBUGGING = 'resources.debug'
	static REQ_ATTR_DISPOSITIONS_REMAINING = 'resources.dispositions.remaining'
	static REQ_ATTR_DISPOSITIONS_DONE = "resources.dispositions.done"
	
	static DISPOSITION_HEAD = 'head'
	static DISPOSITION_DEFER = 'defer'
	static DEFAULT_DISPOSITION_LIST = [DISPOSITION_HEAD, DISPOSITION_DEFER]

	/**
	 * Get the set of dispositions required by resources in the current request, which have not yet been rendered
	 */
	static Set getRequestDispositionsRemaining(request) {
		def dispositions = request[REQ_ATTR_DISPOSITIONS_REMAINING]
		// Return a new set of HEAD + DEFER if there is nothing in the request currently, this is our baseline
		if (dispositions == null) {
			dispositions = new HashSet()
			request[REQ_ATTR_DISPOSITIONS_REMAINING] = dispositions
		}
		return dispositions
	}

	/**
	 * Add a disposition to the current request's set of them
	 */
	static void addDispositionToRequest(request, String disposition, String reason) {
		if (haveAlreadyDoneDispositionResources(request, disposition)) {
			throw new IllegalArgumentException("""Cannot disposition [$disposition] to this request (required for [$reason]) -
that disposition has already been rendered. Check that your r:layoutResources tag comes after all
Resource tags that add content to that disposition.""")
		}
		def dispositions = request[REQ_ATTR_DISPOSITIONS_REMAINING]
		if (dispositions != null) {
			dispositions << disposition
		} else {
			request[REQ_ATTR_DISPOSITIONS_REMAINING] = [disposition] as Set
		}
	}
	
	/**
	 * Add a disposition to the current request's set of them
	 */
	static void removeDispositionFromRequest(request, String disposition) {
		def dispositions = request[REQ_ATTR_DISPOSITIONS_REMAINING]
		if (dispositions != null) {
			dispositions.remove(disposition)
		}
	}
	
	static void doneDispositionResources(request, String disposition) {
		removeDispositionFromRequest(request, disposition)
		def s = request[REQ_ATTR_DISPOSITIONS_DONE]
		if (s == null) {
			s = new HashSet()
			request[REQ_ATTR_DISPOSITIONS_DONE] = s
		}
		s << disposition
	}
	
	static boolean haveAlreadyDoneDispositionResources(request,String disposition) {
		def s = request[REQ_ATTR_DISPOSITIONS_DONE]
		s == null ? false : s.contains(disposition)
	}
   
}
