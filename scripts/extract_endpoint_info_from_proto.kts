import java.io.File

data class EndpointInfo(val method: String, val url: String, val verb: String)


if (args.isEmpty()) {
  println("Error: missing required argument - please provide the path to a proto file")
} else {
  // Define the path to the proto file
  val protoFilePath = args[0]
  val protoFile = File(protoFilePath)

  if (!protoFile.exists()) {
    println("Error: file not found - please provide a valid path to a proto file")
  } else {
    println("Processing file: $protoFilePath")

    // Extract endpoint information from the proto file
    val endpointInfos = extractEndpointInfoFromProto(protoFile)

    // Write endpointInfos to a file
    val outputFilePath = "proto-info-extracted.txt"
    writeEndpointInfosToFile(endpointInfos, outputFilePath)

    println("Successfully wrote endpoint information to $outputFilePath")
  }
}


fun extractEndpointInfoFromProto(protoFile: File): List<EndpointInfo> {
  val protoLines = protoFile.readLines()
  val endpointInfos = mutableListOf<EndpointInfo>()
  var method: String? = null

  protoLines.forEachIndexed { index, line ->
    if (line.trim().startsWith("rpc")) {
      val parts = line.split(" ")
      if (parts.size > 1) {
        method = parts[3]
      } else {
        println("Warning: 'rpc' line at index ${index + 1} does not contain Method Name")
      }
    } else {
      if (line.contains("option (google.api.http) =")) {
        val nextLine = index + 1
        if (nextLine < protoLines.size) {
          val httpLine = protoLines[nextLine].trim()

          val verb = extractHttpVerb(httpLine)
          val url = extractUrl(httpLine)

          if (method != null && verb != null && url != null) {
            val endpointInfo = EndpointInfo(method, url, verb)
            endpointInfos.add(endpointInfo)
          } else {
            println("Failed to extract endpoint at line ${nextLine + 1}")
          }
        } else {
          println("Warning: No line following the HTTP option line at index ${index + 1}")
        }
      }
    }
  }
  return endpointInfos
}

fun extractHttpVerb(line: String): String? {
  val verbs = listOf("post:", "get:", "put:", "delete:")
  val foundVerb = verbs.firstOrNull { line.contains(it) }
  //println("Extracted HTTP verb: $foundVerb from line: $line")
  return foundVerb?.replace(":", "")
}

fun extractUrl(line: String): String? {
  val urlPattern = """"(/[^"]+)"""".toRegex()
  val matchResult = urlPattern.find(line)
  val extractedUrl = matchResult?.groupValues?.get(1)
  //println("Extracted URL: $extractedUrl from line: $line")
  return extractedUrl
}

fun writeEndpointInfosToFile(endpointInfos: List<EndpointInfo>, outputFilePath: String) {
  try {
    File(outputFilePath).printWriter().use { writer ->
      endpointInfos.forEach { info ->
        writer.println("${info.method},${info.url},${info.verb}")
      }
    }
  } catch (e: Exception) {
    println("Error writing to file: ${e.message}")
  }
}
