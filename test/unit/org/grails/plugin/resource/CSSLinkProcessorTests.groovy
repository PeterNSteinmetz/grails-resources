package org.grails.plugin.resource
import grails.test.GrailsUnitTestCase
import org.junit.Rule
import org.junit.rules.TemporaryFolder

class CSSLinkProcessorTests extends GrailsUnitTestCase {
    @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder()
    File temporarySubfolder
    def mockResSvc
    
    void setUp() {
        super.setUp()
        temporarySubfolder = temporaryFolder.newFolder('test-tmp')
        mockResSvc = [
            config : [ rewrite: [css: true] ]
        ]
    }

    protected ResourceMeta makeRes(String reluri, String contents) {
        def r = new ResourceMeta(sourceUrl:'/'+reluri)
        r.workDir = temporarySubfolder
        r.actualUrl = r.sourceUrl
        r.contentType = "text/css"
        r.processedFile = new File(temporarySubfolder, reluri)
        r.processedFile.parentFile.mkdirs()
        r.processedFile.delete()

        r.processedFile << new ByteArrayInputStream(contents.bytes)
        return r
    }
    
    /**
     * This simulates a test where the image resources are moved to a new flat dir
     * but the CSS is *not* moved, to force recalculation of paths
     */
    void testCSSPreprocessing() {

        def res = makeRes('css/urltests1.css', """
.bg1 { background: url(/images/theme/bg1.png) }
.bg2 { background: url("/css/images/bg2.png") }
.bg3 { background: url( /images/bg3.png ) }
.bg4 { background: url( '/css/bg4.png' ) }
.bg5 { background: url(http://google.com/images/bg5.png) }
.bg6 { background: url(https://google.com/images/bg5.png) }
.bg7 { background: url(####BULL) }
.bg8 { background: url(data:font/opentype;base64,ABCDEF123456789ABCDEF123456789) }
.bg9 { background: url(//mydomain.com/protocol-relative-url) }
""")
        def expectedLinks = [
            '/images/theme/bg1.png',
            '/css/images/bg2.png',
            '/images/bg3.png',
            '/css/bg4.png',
            'http://google.com/images/bg5.png',
            'https://google.com/images/bg5.png',
            '####BULL',
            'data:font/opentype;base64,ABCDEF123456789ABCDEF123456789',
            '//mydomain.com/protocol-relative-url'
        ]
        def cursor = 0
        
        def processor = new CSSLinkProcessor()
        processor.process(res, mockResSvc) { prefix, original, suffix ->
            assertEquals expectedLinks[cursor++], original
        }
    }
}