function get_available_pool(stream, state, memory )
    local function map_pool(record)
        return map{PK=record.PK, state=record.state,capacity=record.capacity, serverList=record.serverList}
    end

    local function filter_pool(record)
        return record.capacity >= memory and record.state == state
    end
    return stream : filter(filter_pool) : map(map_pool)
end