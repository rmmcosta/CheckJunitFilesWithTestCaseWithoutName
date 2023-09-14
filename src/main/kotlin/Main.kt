import org.w3c.dom.Document
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

fun main() {
    val junitFiles = readXMLFileNamesFromResources()
    println("Found ${junitFiles.size} files")
    val invalidFileNames = junitFiles.filter(::invalidNameAttribute).map { it.baseURI }
    if (invalidFileNames.isEmpty()) {
        println("All files are valid")
    } else {
        println("Invalid files:")
        invalidFileNames.forEach(::println)
    }
}

fun readXMLFileNamesFromResources(): List<Document> {
    val pathToResourceFiles = "src/main/resources"
    val files = File(pathToResourceFiles).listFiles() ?: throw IllegalStateException("No files found in $pathToResourceFiles")
    return files.map { it.readXml() }
}

fun File.readXml(): Document {
    val dbFactory = DocumentBuilderFactory.newInstance()
    val dBuilder = dbFactory.newDocumentBuilder()
    return dBuilder.parse(this)
}

fun invalidNameAttribute(doc: Document): Boolean {
    val xpFactory = XPathFactory.newInstance()
    val xPathObject = xpFactory.newXPath()
    val xpathToTag = "/testsuites/testsuite/testcase"
    val testcaseNodeList = xPathObject.evaluate(xpathToTag, doc, XPathConstants.NODESET) as NodeList
    for (i in 0..<testcaseNodeList.length) {
        val testcase = testcaseNodeList.item(i)
        if (testcase.attributes.getNamedItem("name") == null || testcase.attributes.getNamedItem("name").nodeValue.isBlank()) {
            return true
        }
    }
    return false
}