import './App.css'
import { useState } from 'react'
import { getFileMD5 } from './MD5Utils';
import { concurrentUploadFile } from './uploadFile';
import { downloadFile } from './downloadFile';

function App() {
  const [file, setFile] = useState(null);

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (file) {
      const md5 = await getFileMD5(file);
      try {
        await concurrentUploadFile(file, md5);
      } catch (error) {
        console.error('Error occurred during file upload:', error);
      }
    }
  };

  return (
    <>
      <form onSubmit={handleSubmit}>
        <input type="file" onChange={handleFileChange}/>
        <button type="submit">Upload File</button>
      </form>
      <button onClick={downloadFile}>Download File</button>
    </>
  );
}

export default App
