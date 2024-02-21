export async function concurrentUploadFile(file, md5) {
  const chunkSize = 10485760  // Read in chunks of 10MB
  const chunks = Math.ceil(file.size / chunkSize)
  let requestList = []

  for (let i = 0; i < chunks; i++) {
    const start = i * chunkSize
    // end 不在 file.slice 的索引中，所以不需要减一
    const end = ((start + chunkSize) >= file.size) ? file.size : start + chunkSize
    const fileChunk = file.slice(start, end)

    console.log('Uploading: ', i + 1, ' of ', chunks);
    requestList.push(uploadRequest({md5, chunkNumber: i, chunks, fileChunk, file}))
  }

  if (requestList?.length) {
    await Promise.all(requestList)
  }
}

async function uploadRequest({md5, chunkNumber, chunks, fileChunk, file}) {
  const formData = new FormData();
  formData.append('md5', md5);
  formData.append('chunkNumber', chunkNumber);
  formData.append('chunks', chunks);
  formData.append('fileChunk', fileChunk);
  formData.append('fileName', file.name);

  return fetch('http://localhost:8080/upload', {
    method: 'POST',
    body: formData,
  }).then(response => {
    if (response.ok) {
      console.log('Chunk uploaded successfully');
    } else {
      console.error('Chunk upload failed');
      throw new Error('Chunk upload failed');
    }
  });
}

export async function uploadFile(file, md5) {
  const chunkSize = 10485760  // Read in chunks of 10MB
  const chunks = Math.ceil(file.size / chunkSize)

  for (let i = 0; i < chunks; i++) {
    const start = i * chunkSize
    const end = ((start + chunkSize) >= file.size) ? file.size : start + chunkSize
    const fileChunk = file.slice(start, end)

    console.log('Uploading: ', i + 1, ' of ', chunks);
    
    const formData = new FormData();
    formData.append('md5', md5);
    formData.append('chunkNumber', i);
    formData.append('chunks', chunks);
    formData.append('fileChunk', fileChunk);
    formData.append('fileName', file.name);

    await fetch('http://localhost:8080/upload', {
      method: 'POST',
      body: formData,
    }).then(response => {
      if (response.ok) {
        console.log('Chunk uploaded successfully');
      } else {
        console.error('Chunk upload failed');
        throw new Error('Chunk upload failed');
      }
    });
  }
}