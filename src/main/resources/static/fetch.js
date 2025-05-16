// Fetch users from JSONPlaceholder API
fetch('https://jsonplaceholder.typicode.com/users')
  .then(response => {
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json();
  })
  .then(data => {
    console.log('Users:', data);
    console.log('First user name:', data[0].name);
  })
  .catch(error => {
    console.error('Error fetching data:', error);
  });

