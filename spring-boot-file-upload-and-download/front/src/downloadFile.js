let response = []
let requestList = []
let countOkResponse = 0

export async function downloadFile() {
  const chunkSize = 10485760  // Read in chunks of 10MB
  const fileSize = 120100568  // Total file size is 120MB
  const chunks = Math.ceil(fileSize / chunkSize)

  for (let i = 0; i < chunks; i++) {
    const start = i * chunkSize
    // end 属于索引，所以需要减一
    const end = ((start + chunkSize) >= fileSize) ? fileSize - 1 : start + chunkSize - 1

    requestList.push(downloadRequest({start, end}).then(handleResolution).catch(handleResolution))
  }

  if (requestList?.length) {
    await Promise.all(requestList)
    console.log('download response: ', response)

    const mergedBlob = new Blob(await Promise.all(response.map((res) => res.blob())), { type: "video/mp4" })
    console.log('blob: ', mergedBlob)
    let downloadLink = document.createElement("a")
    downloadLink.href = URL.createObjectURL(mergedBlob)
    downloadLink.download = "video.mp4"
    downloadLink.click()
    downloadLink.remove()
  }
}

async function downloadRequest({start, end}) {
  return fetch("http://localhost:8080/download", {
    method: "GET",
    headers: {
      Range: `bytes=${start}-${end}`,
    },
    responseType: "blob",
  });
}

function handleResolution(result) {
  response.push(result)

  if (result && result.ok) {
    countOkResponse++
    console.log('Process: ', countOkResponse, '/', requestList.length)
  }
}


// export async function downloadFile() {
//   const chunkSize = 10485760  // Read in chunks of 10MB
//   const fileSize = 120100568  // Total file size is 120MB
//   const chunks = Math.ceil(fileSize / chunkSize)
//   let requestList = []
//
//   for (let i = 0; i < chunks; i++) {
//     const start = i * chunkSize
//     // end 属于索引，所以需要减一
//     const end = ((start + chunkSize) >= fileSize) ? fileSize - 1 : start + chunkSize - 1
//
//     requestList.push(downloadRequest({start, end}).then(handleResolution).catch(handleResolution))
//   }
//
//   if (requestList?.length) {
//     const response = await Promise.all(requestList)
//     console.log('download response: ', response)
//
//     const mergedBlob = new Blob(await Promise.all(response.map((res) => res.blob())), { type: "video/mp4" })
//     console.log('blob: ', mergedBlob)
//     let downloadLink = document.createElement("a")
//     downloadLink.href = URL.createObjectURL(mergedBlob)
//     downloadLink.download = "video.mp4"
//     downloadLink.click()
//     downloadLink.remove()
//   }
// }
//
// async function downloadRequest({start, end}) {
//   return fetch("http://localhost:8080/download", {
//     method: "GET",
//     headers: {
//       Range: `bytes=${start}-${end}`,
//     },
//     responseType: "blob",
//   });
// }
//
// function handleResolution(result) {
//   if (result && result.ok) {
//     console.log('Number of successful promises:', )
//   }
// }