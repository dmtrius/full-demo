async function createPost() {
  try {
    const response = await fetch('https://cmap.jud11.flcourts.org/jisws/jisService/v100/schedulingDivisions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({}),
    });
    if (!response.ok) throw new Error('Network response was not ok');
    const data = await response.json();
    console.log('Response from POST:', data);
  } catch (error) {
    console.error('Error during POST request:', error);
  }
}

createPost();

