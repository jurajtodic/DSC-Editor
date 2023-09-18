import React from "react";
const FilterComponent = ({ filterText, onFilter, onClear }) => (
  <>
    <input
    className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-200 p-2.5 "
      id="search"
      type="text"
      placeholder="Search Configurations..."
      value={filterText}
      onChange={onFilter}
    />
    <button className='px-2 mx-1  bg-gray-600 rounded-full text-[2vh] text-white hover:scale-105 duration-150' onClick={onClear}>Search</button>
  </>
);

export default FilterComponent;
