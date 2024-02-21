import SparkMD5 from 'spark-md5'

export const getFileMD5 = (file) => {
  return new Promise((resolve) => {
    let blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice,
    chunkSize = 10485760,  // Read in chunks of 10MB
    chunks = Math.ceil(file.size / chunkSize),
    currentChunk = 0,
    spark = new SparkMD5.ArrayBuffer(),
    fileReader = new FileReader();

    fileReader.onload = function (e) {
      console.log('read chunk nr', currentChunk + 1, 'of', chunks);
      spark.append(e.target.result);
      currentChunk += 1;
      
      if (currentChunk < chunks) {
        loadNext();
      } else {
        let result = spark.end()
        resolve(result)
      }
    };

    fileReader.onerror = function () {
      console.error('Read file error!');
    };

    const loadNext = () => {
      const start = currentChunk * chunkSize,
        end = ((start + chunkSize) >= file.size) ? file.size : start + chunkSize;
      fileReader.readAsArrayBuffer(blobSlice.call(file, start, end));
    }

    loadNext();
  })
}
