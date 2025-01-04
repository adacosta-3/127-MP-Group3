import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api',
});


// EXAMPLE
// export const getUserDetails = async (userId) => {
//     const response = await api.get(`/users/user/${userId}`);
//     return response.data;
// };