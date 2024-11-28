import { fetchUtils } from 'react-admin';

const apiUrl = 'http://localhost:8080/api'; // Backend API URL

const dataProvider = {
    getList: (resource, params) => {
        const { pagination: { page = 1, perPage = 10 } = {}, sort, filter } = params;
        const apiPage = page - 1;
    
        // Construct the query parameters
        const flatFilter = Object.entries(filter || {}).map(([key, value]) => [key, String(value)]);
        const query = {
            page: apiPage,
            pageSize: perPage,
            sortField: sort?.field || 'id',
            sortOrder: sort?.order || 'ASC',
            ...Object.fromEntries(flatFilter),
        };
    
        const queryString = new URLSearchParams(query).toString();
        const url = `${apiUrl}/${resource}?${queryString}`;
    
        return fetchUtils.fetchJson(url)
            .then(({ json, headers }) => {
                const contentRange = headers.get('Content-Range');
                const total = contentRange ? parseInt(contentRange.split('/')[1], 10) : json.total;
    
                // Resource map to get the correct entity list from the response
                const resourceMap = {
                    users: 'users',
                    permissions: 'permissions',
                    roles: 'roles',
                    teams: 'teams',
                };
                const dataKey = resourceMap[resource] || resource;
                const data = json[dataKey];
    
                if (!data) {
                    return Promise.reject(new Error(`API response does not contain a '${dataKey}' field.`));
                }
    
                return { data, total };
            })
            .catch((error) => {
                console.error(`Error in getList for resource '${resource}':`, error.message);
                return Promise.reject(new Error(`Error fetching data: ${error.message}`));
            });
    },
    
    // Define other methods (getOne, create, update, delete) as needed
};

export default dataProvider;

