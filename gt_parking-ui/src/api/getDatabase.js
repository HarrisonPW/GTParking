async function getAllParkingLots() {
    const get_all_parking_lot_url = 'https://127.0.0.1:8443/parkinglots';
    try {
        // const response = await fetch(get_all_parking_lot_url);
        const response = await fetch(get_all_parking_lot_url);
        const data = await response.json();

        const parkinglotData = data["data"]["result"].map(oldData => ({
              id: oldData.parkinglotid,
              name: oldData.name,
              occupancy: oldData.currentspotsnum / oldData.totalspotsnum * 100,
              coordinates: [oldData.xcoordinate, oldData.ycoordinate]
          }));
        return parkinglotData
    } catch (error) {
        console.error('Failed to fetch:', error);
    }
}

export default getAllParkingLots;

